package com.homike.user.HoBook;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by User on 12/30/2016.
 */

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler(){

    }

    public String makeServiceCall(String reqUrl){
        String response = null;
        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            //read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        }catch(MalformedURLException e){
            Log.e(TAG,"MalformedURLException: " + e.getMessage());
        }catch(ProtocolException e){
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        }catch(IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }catch(Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;
    }

    //to send to web server
    public String makeHttpRequest(String url,String id){

        HttpURLConnection httpURLConnection = null;
        StringBuffer response = null;

        try{

                URL urlPost = new URL(url);
                httpURLConnection = (HttpURLConnection) urlPost.openConnection();
                //set request properties
                httpURLConnection.setDoOutput(true); //defaults request method to POST
                httpURLConnection.setDoInput(true);  //allow input to this HttpURLConnection
                httpURLConnection.setRequestMethod("POST");
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("id="+id);
                wr.flush(); //flush the stream when we're finished writing to make sure all bytes get to their destination
                wr.close();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
        } catch (MalformedURLException e) {
            Log.e(TAG,"MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return response.toString();
    }

        //to create user from DialogBox
    public String postComment(String url,String urlParameters){
        //Making Http request
        HttpURLConnection httpURLConnection = null;
        StringBuffer response = null;
        String lineEnd = "\r\n";

        try{
                URL urlPost = new URL(url);
                httpURLConnection = (HttpURLConnection) urlPost.openConnection();
                httpURLConnection.setDoOutput(true); //defaults request method to POST
                httpURLConnection.setDoInput(true);  //allow input to this HttpURLConnection
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush(); //flush the stream when we're finished writing to make sure all bytes get to their destination
                wr.close();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();

    }

    private String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try{
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                is.close();
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                try{
                    is.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
