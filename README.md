# RetroApollo-Android
[Apollo-Android](https://github.com/apollographql/apollo-android) wrapper like Retrofit for easy use.

Tested on Apollo-Android 0.4.2. It has been deployed to jCenter: 

```groovy
api "com.bennyhuo.retroapollo:retroapollo:0.4.2-beta"
```

## Example

This is based on [Apollo-Android](https://github.com/apollographql/apollo-android), so you should config graphql api as what we do in Apollo-Android.

 Suppose we have graphql request below:

 ```
 query RepositoryStatistics($repo: String!, $owner: String!){
   repository(name: $repo, owner: $owner) {
     stargazers{
       totalCount
     }
     watchers{
       totalCount
     }
     issues{
       totalCount
     }
   }
 }
 ```

We can then create an interface like this:

```kotlin
interface GraphQLService {
    fun repositoryStatisticsQuery(@GraphQLQuery("owner") owner: String, @GraphQLQuery("repo") repo: String): Observable<Data>
}
```

Just like what we do in retrofit, create an instance of interface by RetroApollo.Builder:

```
val apolloClient by lazy {

//Build the Apollo Client
    ApolloClient.builder()
            .okHttpClient(OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build())
            .serverUrl("https://api.github.com/graphql")
            .build()
}

val graphQLService by lazy {
    RetroApollo.Builder()
            .apolloClient(apolloClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory()
                    .observableScheduler(AndroidSchedulers.mainThread())
                    .subscribeScheduler(Schedulers.io()))
            .build()
            .createGraphQLService(GraphQLService::class)
}
```

That's it! Now we can make request like this:

```kotlin
graphQLService.repositoryStatisticsQuery("enbandari", "RetroApollo")
            .subscribe {
                ...
            }
```

# License

```
Copyright 2017 Bennyhuo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
