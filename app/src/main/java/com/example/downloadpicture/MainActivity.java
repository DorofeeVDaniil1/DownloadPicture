package com.example.downloadpicture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button loadButton;
    TextView tv;
    TableRow imageRow;
    ProgressBar progress;

    public String[] imageUrls = {
            "https://klike.net/uploads/posts/2019-06/1560231206_1.jpg",
            "https://klike.net/uploads/posts/2019-06/medium/1560231179_5.jpg",
            "https://klike.net/uploads/posts/2019-06/medium/1560231150_7.jpg"
    };

    ArrayList<ImageView> imageView = new ArrayList<>();//Этот массив заточен только для ImageView
    AsyncLoaderImage loaderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageRow = findViewById(R.id.imageRow);
        loadButton = findViewById(R.id.loadButton);
        progress = findViewById(R.id.progress);
            setupView();//Вызов функции добавления фоток из array в табле row
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaderImage==null || loaderImage.getStatus()==AsyncTask.Status.FINISHED){
                    loaderImage = new AsyncLoaderImage();
                    loaderImage.execute(imageUrls);
                    progress.setProgress(0);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Подождите окончания загрузки",Toast.LENGTH_SHORT).show();
                }


            }
        });





        }
        private void setupView(){
            for (int i = 0; i < imageRow.getChildCount(); i++) {
                imageView.add((ImageView) imageRow.getChildAt(i));
            }

        }


    class AsyncLoaderImage extends AsyncTask<String, Pair<Integer,Bitmap>,Void>{
        //1-данные которые должны прийти(у нас это строки2 потоку(наши адресса),
        // 2-показать на интерфейсе логическую пару итн и картинку,Класс пэир объединяет 2 класса
        // 3- так как мы все уже показли, то из потока мы уже ничего не будем возвращать
        boolean isInerne = true;
        String messegeError="";



        @Override
        protected Void doInBackground(String... strings) {
            for (int i = 0; i < strings.length; i++) {
                try {
                    Bitmap image = getImageBuyUrl(strings[i]);
                    Pair<Integer,Bitmap> pair = new Pair<>(i,image);
                    publishProgress(pair);
                } catch (MalformedURLException e){
                    //Toast.makeText(getApplicationContext(), "Неверный адрес", Toast.LENGTH_SHORT).show();
                    messegeError="Неверный адресс";
                    isInerne = false;
                    Pair<Integer,Bitmap> pair = new Pair<>(0,null);
                    publishProgress(pair);
                }
                catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    messegeError=e.getMessage();
                    isInerne = false;
                    Pair<Integer,Bitmap> pair = new Pair<>(0,null);
                    publishProgress(pair);
                }
            }
            return null;
        }


        private Bitmap getImageBuyUrl(String url) throws MalformedURLException,IOException {
            Bitmap image = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            //getcontent скачивает содержимое по ссылке, но выдает объект.
            //Bitmap = умеет создавать файлы из байтов, файлов.
            return image;
        }

        @Override
        protected void onProgressUpdate(Pair<Integer, Bitmap>... values) {//... - система предпологает, что будет массив(неважно какой важно что система все считает массивом
            super.onProgressUpdate(values);
            if (isInerne) {


                int position = values[0].first;//порядковый номер загруженной картинки
                int currentProgress = position + 1;//для прогресс бара, его обноаления
                Bitmap image = values[0].second;
                //Обновляем прогресс
                progress.setProgress(currentProgress * 100);
                //Показ картинки вариант 1 используем arrayList
                //imageView.get(position).setImageBitmap(image);
                //Вариант 2
                ((ImageView) imageRow.getChildAt(position)).setImageBitmap(image);//Нужно уточнять, потому что TableRow не знает, какой это номер
                progress.setProgress(400);
            }
            else {
                Toast.makeText(getApplicationContext(),messegeError,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Toast.makeText(getApplicationContext(), "Загрузка заверешена", Toast.LENGTH_SHORT).show();
        }
    }

    }
