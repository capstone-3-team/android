package com.knu.quickthink.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.knu.quickthink.UserToken
import com.knu.quickthink.data.UserTokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton




@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule{

    @Provides
    @Singleton
    fun provideUserTokenDataStore(@ApplicationContext context : Context) : UserTokenDataStore{
        return UserTokenDataStore(context.userTokenStore)
    }
}

object UserTokenSerializer : Serializer<UserToken> {
    override val defaultValue: UserToken = UserToken.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserToken {
        try {
            return UserToken.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserToken, output: OutputStream) = t.writeTo(output)
}

private const val DATA_STORE_FILE_NAME = "userToken.pb"
private val Context.userTokenStore : DataStore<UserToken> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserTokenSerializer
)