package com.ujuzi.bingwasokonibot;

import android.content.Context;

import com.ujuzi.bingwasokonibot.listeners.CheckTransactionListener;
import com.ujuzi.bingwasokonibot.listeners.DeleteAccountListener;
import com.ujuzi.bingwasokonibot.listeners.GetOffersListener;
import com.ujuzi.bingwasokonibot.listeners.GetTariffsListener;
import com.ujuzi.bingwasokonibot.listeners.PaymentListener;
import com.ujuzi.bingwasokonibot.listeners.PostLoginListener;
import com.ujuzi.bingwasokonibot.listeners.PostOfferListener;
import com.ujuzi.bingwasokonibot.listeners.PostPersonaListener;
import com.ujuzi.bingwasokonibot.listeners.PostTransactionListener;
import com.ujuzi.bingwasokonibot.listeners.RequestTokenListener;
import com.ujuzi.bingwasokonibot.listeners.ResetPasswordListener;
import com.ujuzi.bingwasokonibot.listeners.STKPushListener;
import com.ujuzi.bingwasokonibot.models.CheckTransactionApiResponse;
import com.ujuzi.bingwasokonibot.models.DeleteAccountApiResponse;
import com.ujuzi.bingwasokonibot.models.DeleteAccountPojo;
import com.ujuzi.bingwasokonibot.models.GetOffersBody;
import com.ujuzi.bingwasokonibot.models.GetOffersResponse;
import com.ujuzi.bingwasokonibot.models.Payment;
import com.ujuzi.bingwasokonibot.models.PostOfferOne;
import com.ujuzi.bingwasokonibot.models.PostPersonaApiResponse;
import com.ujuzi.bingwasokonibot.models.LoginPojo;
import com.ujuzi.bingwasokonibot.models.Persona;
import com.ujuzi.bingwasokonibot.models.PostLoginApiResponse;
import com.ujuzi.bingwasokonibot.models.PostOfferApiResponse;
import com.ujuzi.bingwasokonibot.models.RequestTokenApiResponse;
import com.ujuzi.bingwasokonibot.models.RequestTokenPojo;
import com.ujuzi.bingwasokonibot.models.ResetPasswordApiResponse;
import com.ujuzi.bingwasokonibot.models.ResetPasswordPojo;
import com.ujuzi.bingwasokonibot.models.STKPushPojo;
import com.ujuzi.bingwasokonibot.models.STKPushResponse;
import com.ujuzi.bingwasokonibot.models.TariffApiResponse;
import com.ujuzi.bingwasokonibot.models.Transaction;
import com.ujuzi.bingwasokonibot.models.TransactionApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RequestManager {

    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://app3.airtime2pesa.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private DBHelper helper;

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
    public void postOffer(PostOfferListener listener, PostOfferOne offerPOJO,String authHeader){
        PostOffer postOffer = retrofit.create(PostOffer.class);
        Call<PostOfferApiResponse> call = postOffer.postOffer(authHeader,offerPOJO);
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
    public void postTransaction(PostTransactionListener listener, Transaction transaction, String authHeader){
        PostTransaction postTransaction = retrofit.create(PostTransaction.class);
        Call<TransactionApiResponse> call = postTransaction.postTransaction(authHeader,transaction);
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
    public void stkPush(STKPushListener listener, STKPushPojo stkPushPojo,String authHeader){
        STKPush stkPush = retrofit.create(STKPush.class);
        Call<STKPushResponse> call = stkPush.stkPush(authHeader,stkPushPojo);
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
    public void getPaymentStatus(PaymentListener listener, String tillNumber, String authHeader){
        GetPayment payment = retrofit.create(GetPayment.class);
        Call<Payment> call = payment.getPayment(authHeader, tillNumber);
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
    public void callTariffsApi(GetTariffsListener listener,String authHeader){
        GetTariffs getTariffs = retrofit.create(GetTariffs.class);
        Call<TariffApiResponse> call = getTariffs.getTariffs(authHeader);
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
    public void checkTransactions(CheckTransactionListener listener, String phoneNumber,String authHeader){
        CheckTransaction checkTransaction = retrofit.create(CheckTransaction.class);
        Call<CheckTransactionApiResponse> call = checkTransaction.checkTransaction(authHeader,phoneNumber);
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
    public void resetPassword(ResetPasswordListener listener, ResetPasswordPojo pojo, String authHeader){
        ResetPassword resetPassword = retrofit.create(ResetPassword.class);
        Call<ResetPasswordApiResponse> call = resetPassword.resetPassword(authHeader, pojo);
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
    public void deleteAccount(DeleteAccountListener listener, DeleteAccountPojo pojo, String authHeader){
        DeleteAccount deleteAccount = retrofit.create(DeleteAccount.class);
        Call<DeleteAccountApiResponse> call = deleteAccount.deleteAccount(authHeader,pojo);
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
    public void getOffers (GetOffersListener listener, GetOffersBody getOffersBody, String authHeader){
        GetOffers getOffers = retrofit.create(GetOffers.class);
        Call<List<GetOffersResponse>> call = getOffers.getOffers(authHeader,getOffersBody);
        call.enqueue(new Callback<List<GetOffersResponse>>() {
            @Override
            public void onResponse(Call<List<GetOffersResponse>> call, Response<List<GetOffersResponse>> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(),response.message());
            }

            @Override
            public void onFailure(Call<List<GetOffersResponse>> call, Throwable throwable) {
                listener.didError(throwable.getMessage());
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
        Call<PostOfferApiResponse> postOffer(@Header("Authorization") String authHeader, @Body PostOfferOne postOfferOne);
    }
    private interface GetOffers{
        @POST("api/view-offers")
        Call<List<GetOffersResponse>> getOffers(
                @Header("Authorization") String authHeader,
                @Body GetOffersBody getOffersBody
        );
    }
    private interface PostTransaction {
        @POST("api/transactions")
        Call<TransactionApiResponse> postTransaction(@Header("Authorization") String authHeader, @Body Transaction transaction);
    }
    private interface STKPush{
        @POST("api/stkpush")
        Call<STKPushResponse> stkPush (@Header("Authorization") String authHeader, @Body STKPushPojo stkPushPojo);
    }
    private interface GetPayment{
        @GET("api/successful-payments")
        Call<Payment> getPayment(
                @Header("Authorization") String authHeader,@Query("tillNumber") String tillNumber
        );
    }
    private interface GetTariffs{
        @GET("api/tariffs")
        Call<TariffApiResponse> getTariffs(
                @Header("Authorization") String authHeader
        );
    }
    private interface CheckTransaction{
        @GET("api/bundle/check-transaction")
        Call<CheckTransactionApiResponse> checkTransaction(
                @Header("Authorization") String authHeader, @Query("phoneNumber") String phoneNumber
        );
    }
    private interface RequestToken{
        @POST("api/request-reset")
        Call<RequestTokenApiResponse> requestToken ( @Body RequestTokenPojo pojo);
    }
    private interface ResetPassword{
        @POST("api/update-password")
        Call<ResetPasswordApiResponse> resetPassword (@Header("Authorization") String authHeader, @Body ResetPasswordPojo pojo);
    }
    private interface DeleteAccount{
        @POST("api/delete-account")
        Call<DeleteAccountApiResponse> deleteAccount (@Header("Authorization") String authHeader, @Body DeleteAccountPojo pojo);
    }


}
