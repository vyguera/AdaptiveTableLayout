package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.ViewHolderImpl;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.datasource.TableDataSource;

public class SampleLinkedTableAdapter extends LinkedTableAdapter<ViewHolderImpl> {
    private static final int[] COLORS = new int[]{
            0xffe62a10, 0xffe91e63, 0xff9c27b0, 0xff673ab7, 0xff3f51b5,
            0xff5677fc, 0xff03a9f4, 0xff00bcd4, 0xff009688, 0xff259b24,
            0xff8bc34a, 0xffcddc39, 0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722};

    private final LayoutInflater mLayoutInflater;
    private final TableDataSource<String, String, String, String> mTableDataSource;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;

    public SampleLinkedTableAdapter(Context context, TableDataSource<String, String, String, String> tableDataSource) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableDataSource = tableDataSource;
        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);
    }

    @Override
    public int getRowCount() {
        return mTableDataSource.getRowsCount() - 1;
    }

    @Override
    public int getColumnCount() {
        return mTableDataSource.getColumnsCount() - 1;
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        final TestViewHolder vh = (TestViewHolder) viewHolder;
        Log.e("UpdateItem", "onBindViewHolder: rowIndex = " + row + " | columnIndex = " + column);
        String itemData = mTableDataSource.getItemData(row, column); // skip headers

        if (TextUtils.isEmpty(itemData)) {
            itemData = "";
        }

        itemData = itemData.trim();
        vh.tvText.setVisibility(View.VISIBLE);
        vh.ivImage.setVisibility(View.VISIBLE);
        vh.tvText.setText(itemData);
        Glide.with(vh.ivImage.getContext())
                .load(itemData)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        vh.ivImage.setVisibility(View.INVISIBLE);
                        vh.tvText.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        vh.ivImage.setVisibility(View.VISIBLE);
                        vh.tvText.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(vh.ivImage);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;

        vh.tvText.setText(mTableDataSource.getColumnHeaderData(column));  // skip left top header
        int color = COLORS[column % COLORS.length];
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{ColorUtils.setAlphaComponent(color, 50), 0x00000000});
        gd.setCornerRadius(0f);
        vh.vGradient.setBackground(gd);
        vh.vLine.setBackgroundColor(color);
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(mTableDataSource.getItemData(row, 0));
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText(mTableDataSource.getFirstHeaderData());
    }

    @Override
    public int getColumnWidth(int column) {
        return mColumnWidth;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    //------------------------------------- view holders ------------------------------------------

    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;
        ImageView ivImage;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;
        View vGradient;
        View vLine;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            vGradient = itemView.findViewById(R.id.vGradient);
            vLine = itemView.findViewById(R.id.vLine);
        }
    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
