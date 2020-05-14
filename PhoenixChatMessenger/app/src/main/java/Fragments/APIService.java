package Fragments;

import notification.MyResponse;
import notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
        @Headers({
                "Content-type:application/json",
                "Authorization:key=AAAARAavo08:APA91bHso38vb0wE2kZYuOT1fO72E5oRysESxuxAe0dLAuSjzO4MkIpGNU2OlwTWw0zJFPspqFJOSKRedqz5enKroZdF7rqJKzUIE5UYKdvNprdh9oEbuykDQvNHGhJMKWCLqz4eUaEW"
        }
        )
        @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

