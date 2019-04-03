package pl.pwr.s230473.serwisy;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;


public class DownloadService extends Service {

    // Ustawiamy parametry niezbędne nam do notyfikacji
    private static final String CHANNEL_ID = "DownloadID";
    private int notificationId;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private int PROGRESS_MAX = 100;
    private int PROGRESS_CURRENT = 0;

    @Override
    public void onCreate() {
    }

    // onStartCommand jest uruchamiane przy każdym starcie serwisu
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Pobierany nasze ekstra parametry które zostały podane w MainActivity
        Bundle extras = intent.getExtras();
        //Nasz element jest opisany nazwą "url" i przypisujemy jego zawartość do zmiennej url
        String url = String.valueOf(extras.get("url"));
        //Pokazujemy powiadomienie o tym, że pobieranie zostału uruchomione
        Toast.makeText(this, "Uruchomiono pobieranie\n" + url, Toast.LENGTH_SHORT).show();

        //Budujemy naszą notyfikacje o statusie pobieraniu
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Picture Download") // Tytuł naszej notyfikacji
                .setContentText("Download in progress") // I treść naszej notyfikacji
                .setSmallIcon(R.drawable.ic_launcher_background) //Ikona wyświetlana na pasku
                .setPriority(NotificationCompat.PRIORITY_LOW); //Priorytet notyfikacji
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false); //Ustawiamy pasek postępu
        notificationId = startId; // Przypisujemy dla naszej notyfikacji id - w tym wypadku id serwisu
        notificationManager.notify(notificationId, builder.build()); // i tak przygotowany notifikator pokazujemy

        //Ten fragment odpowiada za uruchomienie pobierania naszego pliku.
        /*Wpis address ustawia na sztywno co będziemy pobierać - jeżeli chcemy pobierać z tego
        co podaliśmy na ekranie jako "url" należy to usunąć i w download.execute wpisać "url" zamiast "address" */
        String address = "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg";
        Download download =  new Download(notificationId, notificationManager, builder);
        download.execute(address);

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() { }
}
