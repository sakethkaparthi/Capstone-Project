package sakethkaparthi.fileio.networkclients;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by saketh on 18/10/16.
 */

public class RetrofitClient {
    private static final String API_URL = "https://file.io";

    public interface FileIoService {
        @Multipart
        @POST("/")
        Call<JsonObject> uploadImage(@Part MultipartBody.Part file);
    }

    public static FileIoService getAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FileIoService.class);
    }
}
