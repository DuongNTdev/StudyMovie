/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yepeer.le.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import wseemann.media.FFmpegMediaMetadataRetriever;
import yepeer.le.ChooseLangActivity;
import yepeer.le.MainActivity;
import yepeer.le.Metadata;
import yepeer.le.MetadataLoader;
import yepeer.le.R;
import yepeer.le.Utils;
import yepeer.le.fragments.MyVideoFragment;
import yepeer.le.objects.FilmModel;
import yepeer.le.save.UserData;

public class FilmHeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private ArrayList<FilmModel> mItems;
    private View mHeaderView;
    private EditAtPositionCallback editAtPositionCallback;
    private int identityId;

    public FilmHeaderRecyclerAdapter(Fragment fragment, ArrayList<FilmModel> items, View headerView,
                                     EditAtPositionCallback editAtPositionCallback) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        this.editAtPositionCallback = editAtPositionCallback;
        mItems = items;
        mHeaderView = headerView;
        this.fragment = fragment;
    }

    public FilmModel getItemAtPosition(int position) {
        return mItems.get(position);
    }

    public void remove(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void add(FilmModel f, int position) {
        mItems.add(position, f);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return mItems.size();
        } else {
            return mItems.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            return new ItemViewHolder(mInflater.inflate(R.layout.item_my_video, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ItemViewHolder) {
            final PopupMenu popupMenu = new PopupMenu(fragment.getActivity(), ((ItemViewHolder) viewHolder).imgMore);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_film, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        editAtPositionCallback.onWillEdit(getItemAtPosition(position - 1));
                    } else if (item.getItemId() == R.id.action_delete) {
                        FilmModel f = getItemAtPosition(position - 1);
                        if (f != null) {
                            FilmModel.delete(FilmModel.class, f.getId());
                            mItems.remove(position - 1);
                            notifyDataSetChanged();
                        }
                    }
                    return true;
                }
            });
            ((ItemViewHolder) viewHolder).imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            final FilmModel film = getItemAtPosition(position - 1);
            if(new File(film.getFilmPath()).exists()) {
                Bundle bundle = new Bundle();

                try {
                    bundle.putString("uri", URLDecoder.decode(film.getFilmPath(), "UTF-8"));
                    identityId++;
                    fragment.getLoaderManager().initLoader(identityId, bundle, new LoaderManager.LoaderCallbacks<List<Metadata>>() {
                        @Override
                        public Loader<List<Metadata>> onCreateLoader(int id, Bundle args) {
                            return new MetadataLoader(fragment.getActivity(), args);
                        }

                        @Override
                        public void onLoadFinished(Loader<List<Metadata>> loader, List<Metadata> data) {
                            String title = film.getFilmPath();
                            String des = film.getSubtitlePath();
                            String duration = "";
                            String author = "";

                            StringBuilder b = new StringBuilder();
                            long dur = 0;
                            boolean isLoadedimage = false;
                            for (Metadata m : data) {
                                if (m.getKey().equals(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST)) {
                                    author = m.getValue().toString();
                                } else if (m.getKey().equals(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)) {
                                    try {
                                        dur = Long.parseLong(m.getValue().toString());
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                } else if (m.getKey().equals("image")) {
                                    ((ItemViewHolder) viewHolder).imgAvatar.setImageBitmap((Bitmap) m.getValue());
                                    isLoadedimage = true;
                                }
                            }

                            duration = String.format("%d min, %d sec",
                                    TimeUnit.MILLISECONDS.toMinutes(dur),
                                    TimeUnit.MILLISECONDS.toSeconds(dur) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur))
                            );

                            ((ItemViewHolder) viewHolder).tvFilmName.setText(Utils.getNameOfLink(title));
                            ((ItemViewHolder) viewHolder).tvDuration.setText(duration);
                            ((ItemViewHolder) viewHolder).tvAuthor.setText(author);
                            ((ItemViewHolder) viewHolder).tvDescription.setText(Utils.getNameOfLink(des));
                            if (!isLoadedimage) {
                                ((ItemViewHolder) viewHolder).imgAvatar.setImageResource(R.drawable.white);
                            }
                        }

                        @Override
                        public void onLoaderReset(Loader<List<Metadata>> loader) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                ((ItemViewHolder) viewHolder).imgAvatar.setImageResource(R.drawable.white);
                ((ItemViewHolder) viewHolder).tvFilmName.setText(Utils.getNameOfLink(film.getFilmPath()));
                ((ItemViewHolder) viewHolder).tvDuration.setText(Utils.getTimeString(0));
                ((ItemViewHolder) viewHolder).tvAuthor.setText("");
                ((ItemViewHolder) viewHolder).tvDescription.setText(Utils.getNameOfLink(film.getSubtitlePath()));
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserData.getInstance().getTargetForeign(fragment.getActivity()) == null) {
                        new MaterialDialog.Builder(fragment.getActivity())
                                .title(fragment.getResources().getString(R.string.warning))
                                .content(fragment.getResources().getString(R.string.need_choose_country))
                                .positiveText(fragment.getResources().getString(R.string.argee))
                                .cancelable(false)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        Intent i = new Intent(fragment.getActivity(), ChooseLangActivity.class);
                                        fragment.getActivity().startActivity(i);
                                    }
                                }).show();
                    } else {
                        chooseFilm(film);
                    }
                }
            });
        }
    }

    private void chooseFilm(FilmModel film) {
        MyVideoFragment f = ((MainActivity) fragment.getActivity()).getMyVideoFragment();
        if (f == null) {
            f = MyVideoFragment.newInstance(film.getId());
            ((MainActivity) fragment.getActivity()).setMyVideoFragment(f);
            fragment.getFragmentManager().beginTransaction().add(R.id.root, f).commit();
        } else {
            if (f.getFilmModel().getId() == film.getId()) {
                f.maximize();
            } else {
                fragment.getFragmentManager().beginTransaction().remove(f).commit();
                ((MainActivity) fragment.getActivity()).setMyVideoFragment(null);
                f = MyVideoFragment.newInstance(film.getId());
                fragment.getFragmentManager().beginTransaction().add(R.id.root, f).commit();
                ((MainActivity) fragment.getActivity()).setMyVideoFragment(f);
            }
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgMore, imgAvatar;
        public TextView tvFilmName, tvDescription, tvAuthor, tvDuration;

        public ItemViewHolder(View view) {
            super(view);

            imgMore = ImageView.class.cast(view.findViewById(R.id.imgMore));
            tvFilmName = TextView.class.cast(view.findViewById(R.id.tvFilmName));
            tvDescription = TextView.class.cast(view.findViewById(R.id.tvDescription));
            tvAuthor = TextView.class.cast(view.findViewById(R.id.tvAuthor));
            tvDuration = TextView.class.cast(view.findViewById(R.id.tvDuration));
            imgAvatar = ImageView.class.cast(view.findViewById(R.id.imgAvatar));
        }
    }

    public static interface EditAtPositionCallback {
        public void onWillEdit(FilmModel f);
    }

}
