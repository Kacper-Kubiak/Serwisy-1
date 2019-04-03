package pl.pwr.s230473.serwisy;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Download extends AsyncTask<String, String, String> {

    //Ustawienia dotyczące notifikacji podawane w konstruktorze oraz informacje o ścieżce pobieranego pliku
    private static final String TAG = "INFO";
    private String fileName;
    private String folder;
    private int notificationId;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;

    public Download(int notificationId, NotificationManagerCompat notificationManager, NotificationCompat.Builder builder) {
        this.notificationId = notificationId;
        this.notificationManager = notificationManager;
        this.builder = builder;
    }

    // Czynności ktore zostaną wykonane przez wykonaniem czynności zadanej
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // Główny fragment pobierania w naszym przypadku zdjęcia
    @Override
    protected String doInBackground(String... f_url) {
        //Ustawiamy parametry niezbędne do uruchomienia pobierania
        int count;
        try {
            URL url = new URL(f_url[0]); // ustawiamy adres który podaliśmy w download.execute(url);
            URLConnection connection = url.openConnection(); // Łączymy się z adresem
            connection.connect();
            int lengthOfFile = connection.getContentLength(); // ustawiamy 'długość' pliku
            InputStream input = new BufferedInputStream(url.openStream(), 8192); //Ustawiamy bufor połączenia
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // pobieramy i układamy date w odpowiednim formacie co wykorzystamy w nazwie pliku
            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length()); // Ustawiamy nazwę pliku
            fileName = timestamp + "_" + fileName; // Jeżeli chcemy aby była sama nazwa pliku czy coś to trzeba to odpowiednio zmodyfikować
            /*Miejsce do którego pobieramy nasz plik.
            * getExternalStoragePublicDirectory <- na karcie SD w katalogach publicznyc
            * Environment.DIRECTORY_DOWNLOADS <- folder który jest oznaczony jako "pobrane"*/
            folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;
            File directory = new File(folder);
            // Jeżeli folder nie istnieje to go tworzymy. Przydatne gdy ustawimy inną ścieżke pobierania
            // lub gdy użytkownik sam wybiera gdzie chce pobierać
            if (!directory.exists()) {
                directory.mkdirs();
            }
            OutputStream output = new FileOutputStream(folder + fileName); // Określamy gdzie dokonamy zapisu naszego pliku
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                int progress = (int) ((total * 100) / lengthOfFile);
                publishProgress("" + progress);
                Log.d(TAG, "Progress: " + progress);
                // Aktualizujemy naszą notyfikacje podajac aktualny status pobierania.
                builder.setProgress(100, progress, false);
                notificationManager.notify(notificationId, builder.build());
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            Log.i("test",folder + fileName); // Tutaj dla pewnosci wrzucamy w logi informacje o tym gdzie wyląduje nasz plik i jak się nazywa.
            return "Downloaded at: " + folder + fileName;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return "Something went wrong";
    }

    // funkcja która wykonuje się przy aktualizacji zadania
    // W tej funkcji mozemy umieścić np nasz notyfikator i zmiane jego postępu pobierania
    @Override
    protected void onProgressUpdate(String... progress) {
    }

    // i funkcja wykonująca się po skończeniu zadania
    @Override
    protected void onPostExecute(String message) {
        //Ustawiamy notyfikacje, że udało nam się pobrać
        builder.setContentText("Download complete")
                .setProgress(0,0,false); // zerujemy pasek postępu czyli go wyłączamy
        notificationManager.notify(notificationId, builder.build()); // i pokazujemy/aktualizujemy nasze notyfikacje
        Log.i(TAG, "Koniec");
    }
}
