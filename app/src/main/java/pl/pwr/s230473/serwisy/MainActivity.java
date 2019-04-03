package pl.pwr.s230473.serwisy;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Budujemy Serwis w klasie DownloadService
        final Intent intent = new Intent(this, DownloadService.class);

        //Przypisujemy wartości przycisku i pola edycyjnego
        final Button downloadButton = findViewById(R.id.downloadButton);
        final TextView urlText = findViewById(R.id.urlText);

        //Ustawiamy nasłuchiwanie na wciśnięcie przycisku
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Jeżeli przycisk pobierania został uruchomiony to wpisujemy adres do pobrania jako parametr o nazwie "url"
                 i uruchamiamy nasz serwis "intent". */
                intent.putExtra("url", urlText.getText());
                startService(intent);
            }
        });
    }
}
