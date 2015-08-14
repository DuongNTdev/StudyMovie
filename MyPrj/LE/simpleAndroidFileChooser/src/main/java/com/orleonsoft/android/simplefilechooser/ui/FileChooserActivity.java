package com.orleonsoft.android.simplefilechooser.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.FileInfo;
import com.orleonsoft.android.simplefilechooser.R;
import com.orleonsoft.android.simplefilechooser.adapters.FileArrayAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private File currentFolder;
    private FileArrayAdapter fileArrayListAdapter;
    private FileFilter fileFilter;
    private File fileSelected;
    private ArrayList<String> extensions;
    private ListView lstView;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        lstView = (ListView) findViewById(R.id.lstView);
        toolbar = Toolbar.class.cast(findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!currentFolder.getName().equals(
                        Environment.getExternalStorageDirectory().getName()))
                        && (currentFolder.getParentFile() != null)) {
                    currentFolder = currentFolder.getParentFile();
                    fill(currentFolder);
                } else {
                    Log.i("FILE CHOOSER", "canceled");
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras
                    .getStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS) != null) {
                extensions = extras
                        .getStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS);
                fileFilter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return ((pathname.isDirectory()) || (pathname.getName()
                                .contains(".") ? extensions.contains(pathname
                                .getName().substring(
                                        pathname.getName().lastIndexOf(".")))
                                : false));
                    }
                };
            }
        }
        currentFolder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        fill(currentFolder);
        lstView.setOnItemClickListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!currentFolder.getName().equals(
                    Environment.getExternalStorageDirectory().getName()))
                    && (currentFolder.getParentFile() != null)) {
                currentFolder = currentFolder.getParentFile();
                fill(currentFolder);
            } else {
                Log.i("FILE CHOOSER", "canceled");
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void fill(File f) {
        File[] folders = null;
        if (fileFilter != null)
            folders = f.listFiles(fileFilter);
        else
            folders = f.listFiles();

        this.setTitle(getString(R.string.currentDir) + ": " + f.getName());
        List<FileInfo> dirs = new ArrayList<FileInfo>();
        List<FileInfo> files = new ArrayList<FileInfo>();
        try {
            for (File file : folders) {
                if (file.isDirectory() && !file.isHidden())
                    //si es un directorio en el data se ponemos la contante folder
                    dirs.add(new FileInfo(file.getName(),
                            Constants.FOLDER, file.getAbsolutePath(),
                            true, false));
                else {
                    if (!file.isHidden())
                        files.add(new FileInfo(file.getName(),
                                getString(R.string.fileSize) + ": "
                                        + file.length(),
                                file.getAbsolutePath(), false, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(dirs);
        Collections.sort(files);
        dirs.addAll(files);
//        if (!f.getName().equalsIgnoreCase(
//                Environment.getExternalStorageDirectory().getName())) {
//            if (f.getParentFile() != null)
                //si es un directorio padre en el data se ponemos la contante adeacuada
//                dirs.add(0, new FileInfo("..",
//                        Constants.PARENT_FOLDER, f.getParent(),
//                        false, true));
//        }
        fileArrayListAdapter = new FileArrayAdapter(FileChooserActivity.this,
                R.layout.file_row, dirs);
        lstView.setAdapter(fileArrayListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo fileDescriptor = fileArrayListAdapter.getItem(position);
        if (fileDescriptor.isFolder() || fileDescriptor.isParent()) {
            currentFolder = new File(fileDescriptor.getPath());
            fill(currentFolder);
        } else {

            fileSelected = new File(fileDescriptor.getPath());
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_FILE_SELECTED,
                    fileSelected.getAbsolutePath());

            setResult(Activity.RESULT_OK, intent);
            Log.i("FILE CHOOSER", "result ok");
            finish();
        }
    }
}