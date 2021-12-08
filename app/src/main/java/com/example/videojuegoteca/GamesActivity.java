package com.example.videojuegoteca;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class GamesActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        ListView lvGames = this.findViewById(R.id.lvGames);
        ImageButton btnAdd = this.findViewById(R.id.btnAdd);
        Spinner spnPlatform = this.findViewById(R.id.spnPlatform);
        View header = getLayoutInflater().inflate(R.layout.header_games, null);

        final Intent sendData = this.getIntent();
        login = sendData.getExtras().getString("login", "ERROR");

        lvGames.addHeaderView(header);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addGame = new Intent(GamesActivity.this, AddGameActivity.class);
                activityResultLauncher.launch(addGame);
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent gameData = result.getData();
                            String name = gameData.getExtras().getString("name", "ERROR");
                            String platform = gameData.getExtras().getString("platform", "ERROR");
                            int completed = gameData.getExtras().getInt("completed", 0);
                            int favourite = gameData.getExtras().getInt("favourite", 0);

                            if(GamesActivity.this.gestorDB.addGame(name, platform, completed, favourite, login)){
                                Toast t = Toast.makeText(GamesActivity.this, "The game was registered",
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }else{
                                Toast t = Toast.makeText(GamesActivity.this, "There was a problem registering the game",
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }
                            GamesActivity.this.adapterDB.changeCursor(GamesActivity.this.gestorDB.getGames(login));
                        }
                    }
                };
        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        lvGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Cursor cursor = GamesActivity.this.adapterDB.getCursor();

                if(cursor.moveToPosition(pos-1)){
                    final String name = cursor.getString(0);
                    final String platform = cursor.getString(1);
                    final int completed = cursor.getInt(2);
                    final int favourite = cursor.getInt(3);
                    Intent addGameActivity = new Intent(GamesActivity.this, AddGameActivity.class);

                    addGameActivity.putExtra("name", name);
                    addGameActivity.putExtra("platform", platform);
                    addGameActivity.putExtra("completed", completed);
                    addGameActivity.putExtra("favourite", favourite);

                    activityResultLauncher.launch(addGameActivity);
                }else{
                    Log.e("onContext_edit", "wrong position");
                    Toast t = Toast.makeText(GamesActivity.this, "Wrong position",
                            Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
            }
        });

        spnPlatform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    String platform = spnPlatform.getItemAtPosition(position).toString();
                    Cursor cursorGames = GamesActivity.this.gestorDB.getGamesPlatform(platform, login);
                    GamesActivity.this.adapterDB.changeCursor(cursorGames);
                }else{
                    GamesActivity.this.adapterDB.changeCursor(GamesActivity.this.gestorDB.getGames(login));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.registerForContextMenu(lvGames);
        this.gestorDB = new DBManager(this.getApplicationContext());
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvGames = this.findViewById( R.id.lvGames );

        this.adapterDB = new SimpleCursorAdapter(
                this,
                R.layout.lvgames_item,
                null,
                new String[] { DBManager.GAME_COL_NAME, DBManager.GAME_COL_PLATFORM, DBManager.GAME_COL_COMPLETED, DBManager.GAME_COL_FAVOURITE },
                new int[] { R.id.lvGames_Item_Name, R.id.lvGames_Item_Platform, R.id.lvGames_Item_Completed, R.id.lvGames_Item_Favourite},
                0
        );

        lvGames.setAdapter( this.adapterDB );
        this.adapterDB.changeCursor(this.gestorDB.getGames(GamesActivity.this.login));
    }

    @Override
    public void onPause()
    {
        super.onPause();

        this.gestorDB.close();
        this.adapterDB.getCursor().close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        this.getMenuInflater().inflate(R.menu.actions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        boolean toret = false;

        switch(menuItem.getItemId()){
            case R.id.opInfo:
                Intent infoActivity = new Intent(GamesActivity.this, InfoActivity.class);
                activityResultLauncher.launch(infoActivity);
                toret = true;
                break;
            case R.id.opCode:
                Intent codeActivity = new Intent(GamesActivity.this, CodeActivity.class);
                activityResultLauncher.launch(codeActivity);
                toret = true;
                break;
        }

        return toret;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo cmi){
        super.onCreateContextMenu(contextMenu, view, cmi);

        if(view.getId() == R.id.lvGames){
            this.getMenuInflater().inflate(R.menu.context_menu, contextMenu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        boolean toret = super.onContextItemSelected(item);
        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        Cursor cursor = this.adapterDB.getCursor();

        switch(item.getItemId()){
            case R.id.context_op_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(GamesActivity.this);
                builder.setTitle("WARNING");
                builder.setMessage("Are you sure you want to delete the game?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(cursor.moveToPosition(pos-1)){
                            final String id = cursor.getString(0);
                            if(GamesActivity.this.gestorDB.deleteGame(id, GamesActivity.this.login)){
                                Toast t = Toast.makeText(GamesActivity.this, "The game was deleted",
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();

                            }else{
                                Toast t = Toast.makeText(GamesActivity.this, "There was a problem deleting the game",
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }
                            GamesActivity.this.adapterDB.changeCursor(GamesActivity.this.gestorDB.getGames(login));
                        }else{
                            Log.e("onContext_delete", "wrong position");
                            Toast t = Toast.makeText(GamesActivity.this, "Wrong position",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.context_op_edit:
                if(cursor.moveToPosition(pos-1)){
                    final String name = cursor.getString(0);
                    final String platform = cursor.getString(1);
                    final int completed = cursor.getInt(2);
                    final int favourite = cursor.getInt(3);
                    Intent addGameActivity = new Intent(GamesActivity.this, AddGameActivity.class);

                    addGameActivity.putExtra("name", name);
                    addGameActivity.putExtra("platform", platform);
                    addGameActivity.putExtra("completed", completed);
                    addGameActivity.putExtra("favourite", favourite);

                    activityResultLauncher.launch(addGameActivity);
                    toret = true;
                }else{
                    Log.e("onContext_edit", "wrong position");
                    Toast t = Toast.makeText(GamesActivity.this, "Wrong position",
                            Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
                break;
        }
        return toret;
    }



    private DBManager gestorDB;
    private SimpleCursorAdapter adapterDB;
}
