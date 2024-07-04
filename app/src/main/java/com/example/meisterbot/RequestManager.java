package com.example.meisterbot;

import android.content.Context;
import android.widget.Toast;

import com.example.meisterbot.listeners.CheckTransactionListener;
import com.example.meisterbot.listeners.DeleteAccountListener;
import com.example.meisterbot.listeners.GetOffersListener;
import com.example.meisterbot.listeners.GetTariffsListener;
import com.example.meisterbot.listeners.PaymentListener;
import com.example.meisterbot.listeners.PostLoginListener;
import com.example.meisterbot.listeners.PostOfferListener;
import com.example.meisterbot.listeners.PostPersonaListener;
import com.example.meisterbot.listeners.PostTransactionListener;
import com.example.meisterbot.listeners.RequestTokenListener;
import com.example.meisterbot.listeners.ResetPasswordListener;
import com.example.meisterbot.listeners.STKPushListener;
import com.example.meisterbot.models.CheckTransactionApiResponse;
import com.example.meisterbot.models.DeleteAccountApiResponse;
import com.example.meisterbot.models.DeleteAccountPojo;
import com.example.meisterbot.models.GetOffersBody;
import com.example.meisterbot.models.GetOffersList;
import com.example.meisterbot.models.Payment;
import com.example.meisterbot.models.PostOfferOne;
import com.example.meisterbot.models.PostPersonaApiResponse;
import com.example.meisterbot.models.GetOffersResponse;
import com.example.meisterbot.models.LoginPojo;
import com.example.meisterbot.models.Persona;
import com.example.meisterbot.models.PostLoginApiResponse;
import com.example.meisterbot.models.PostOfferApiResponse;
import com.example.meisterbot.models.RequestTokenApiResponse;
import com.example.meisterbot.models.RequestTokenPojo;
import com.example.meisterbot.models.ResetPasswordApiResponse;
import com.example.meisterbot.models.ResetPasswordPojo;
import com.example.meisterbot.models.STKPushPojo;
import com.example.meisterbot.models.STKPushResponse;
import com.example.meisterbot.models.TariffApiResponse;
import com.example.meisterbot.models.Transaction;
import com.example.meisterbot.models.TransactionApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {

    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://app3.airtime2pesa.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void postPersona(PostPersonaListener listener, Persona persona){
        PostPersona postPersona = retrofit.create(PostPersona.class);
        Call<PostPersonaApiResponse> call = postPersona.postPersona(persona);
        call.enqueue(new Callback<PostPersonaApiResponse>() {
            @Override
            public void onResponse(Call<PostPersonaApiResponse> call, Response<PostPersonaApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<PostPersonaApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }
    public void postOffer(PostOfferListener listener, PostOfferOne offerPOJO){
        PostOffer postOffer = retrofit.create(PostOffer.class);
        Call<PostOfferApiResponse> call = postOffer.postOffer(offerPOJO);
        call.enqueue(new Callback<PostOfferApiResponse>() {
            @Override
            public void onResponse(Call<PostOfferApiResponse> call, Response<PostOfferApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" failed from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<PostOfferApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" failed from on failure");
            }
        });
    }
    public void postLogin(PostLoginListener listener,LoginPojo login){
        PostLogin postLogin = retrofit.create(PostLogin.class);
        Call<PostLoginApiResponse> call = postLogin.postLogin(login);
        call.enqueue(new Callback<PostLoginApiResponse>() {
            @Override
            public void onResponse(Call<PostLoginApiResponse> call, Response<PostLoginApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<PostLoginApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+ " error from on failure");
            }
        });
    }
    public void postTransaction(PostTransactionListener listener, Transaction transaction){
        PostTransaction postTransaction = retrofit.create(PostTransaction.class);
        Call<TransactionApiResponse> call = postTransaction.postTransaction(transaction);
        call.enqueue(new Callback<TransactionApiResponse>() {
            @Override
            public void onResponse(Call<TransactionApiResponse> call, Response<TransactionApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message()+" from on response");
            }

            @Override
            public void onFailure(Call<TransactionApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" from on failure");
            }
        });
    }
    public void stkPush(STKPushListener listener, STKPushPojo stkPushPojo){
        STKPush stkPush = retrofit.create(STKPush.class);
        Call<STKPushResponse> call = stkPush.stkPush(stkPushPojo);
        call.enqueue(new Callback<STKPushResponse>() {
            @Override
            public void onResponse(Call<STKPushResponse> call, Response<STKPushResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                    listener.didFetch(response.body(), response.message());

            }

            @Override
            public void onFailure(Call<STKPushResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });

    }
    public void getOffers (GetOffersListener listener, GetOffersBody getOffersBody){
        GetOffers  getOffers = retrofit.create(GetOffers.class);
        Call<GetOffersList> call = getOffers.getOffers(getOffersBody);
        call.enqueue(new Callback<GetOffersList>() {
            @Override
            public void onResponse(Call<GetOffersList> call, Response<GetOffersList> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" failed from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());

            }

            @Override
            public void onFailure(Call<GetOffersList> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" failed from on failure");
            }
        });
    }
    public void getPaymentStatus(PaymentListener listener, String tillNumber){
        GetPayment payment = retrofit.create(GetPayment.class);
        Call<Payment> call = payment.getPayment(tillNumber);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" failed from onResponse");
                    return;
                }
                listener.didFetch(response.body(), response.message());

            }

            @Override
            public void onFailure(Call<Payment> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" failed from onFailure");
            }
        });
    }

    public void callTariffsApi(GetTariffsListener listener){
        GetTariffs getTariffs = retrofit.create(GetTariffs.class);
        Call<TariffApiResponse> call = getTariffs.getTariffs();
        call.enqueue(new Callback<TariffApiResponse>() {
            @Override
            public void onResponse(Call<TariffApiResponse> call, Response<TariffApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());

            }

            @Override
            public void onFailure(Call<TariffApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }

    public void checkTransactions(CheckTransactionListener listener, String phoneNumber){
        CheckTransaction checkTransaction = retrofit.create(CheckTransaction.class);
        Call<CheckTransactionApiResponse> call = checkTransaction.checkTransaction(phoneNumber);
        call.enqueue(new Callback<CheckTransactionApiResponse>() {
            @Override
            public void onResponse(Call<CheckTransactionApiResponse> call, Response<CheckTransactionApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<CheckTransactionApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }
    public void requestToken(RequestTokenListener listener, RequestTokenPojo pojo){
        RequestToken requestToken = retrofit.create(RequestToken.class);
        Call<RequestTokenApiResponse> call = requestToken.requestToken(pojo);
        call.enqueue(new Callback<RequestTokenApiResponse>() {
            @Override
            public void onResponse(Call<RequestTokenApiResponse> call, Response<RequestTokenApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from onResponse");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RequestTokenApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from onFailure");
            }
        });
    }

    public void resetPassword(ResetPasswordListener listener, ResetPasswordPojo pojo){
        ResetPassword resetPassword = retrofit.create(ResetPassword.class);
        Call<ResetPasswordApiResponse> call = resetPassword.resetPassword(pojo);
        call.enqueue(new Callback<ResetPasswordApiResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordApiResponse> call, Response<ResetPasswordApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from onResponse");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ResetPasswordApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from onFailure");
            }
        });
    }
    public void deleteAccount(DeleteAccountListener listener, DeleteAccountPojo pojo){
        DeleteAccount deleteAccount = retrofit.create(DeleteAccount.class);
        Call<DeleteAccountApiResponse> call = deleteAccount.deleteAccount(pojo);
        call.enqueue(new Callback<DeleteAccountApiResponse>() {
            @Override
            public void onResponse(Call<DeleteAccountApiResponse> call, Response<DeleteAccountApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from onResponse");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<DeleteAccountApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from onFailure");
            }
        });
    }

    private interface PostPersona {
        @POST("api/bingwa_credentials")
        Call<PostPersonaApiResponse> postPersona(@Body Persona persona);
    }
    private interface PostLogin{
        @POST("api/login")
        Call<PostLoginApiResponse> postLogin(@Body LoginPojo loginPojo);
    }
    private interface PostOffer {
        @POST("api/offers")
        Call<PostOfferApiResponse> postOffer(@Body PostOfferOne postOfferOne);
    }
    private interface GetOffers{
        @POST("api/view-offers")
        Call<GetOffersList> getOffers(
                @Body GetOffersBody getOffersBody
        );
    }
    private interface PostTransaction {
        @POST("api/transactions")
        Call<TransactionApiResponse> postTransaction(@Body Transaction transaction);
    }
    private interface STKPush{
        @POST("api/stkpush")
        Call<STKPushResponse> stkPush (@Body STKPushPojo stkPushPojo);
    }
    private interface GetPayment{
        @GET("api/successful-payments")
        Call<Payment> getPayment(
                @Query("tillNumber") String tillNumber
        );
    }
    private interface GetTariffs{
        @GET("api/tariffs")
        Call<TariffApiResponse> getTariffs();
    }
    private interface CheckTransaction{
        @GET("api/bundle/check-transaction")
        Call<CheckTransactionApiResponse> checkTransaction(
                @Query("phoneNumber") String phoneNumber
        );
    }
    private interface RequestToken{
        @POST("api/request-reset")
        Call<RequestTokenApiResponse> requestToken (@Body RequestTokenPojo pojo);
    }
    private interface ResetPassword{
        @POST("api/update-password")
        Call<ResetPasswordApiResponse> resetPassword (@Body ResetPasswordPojo pojo);
    }
    private interface DeleteAccount{
        @POST("api/delete-account")
        Call<DeleteAccountApiResponse> deleteAccount (@Body DeleteAccountPojo pojo);
    }
}
