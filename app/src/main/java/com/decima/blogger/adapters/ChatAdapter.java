package com.decima.blogger.adapters;

import static utils.Constance._MESSAGE_TYPE_TEXT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.decima.blogger.R;
import com.decima.blogger.activity.ChatActivity;
import com.decima.blogger.db.DatabaseService;
import com.decima.blogger.model.ChatItemModel;

import java.util.ArrayList;
import java.util.List;

import utils.Constance;
import utils.Utils;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int type_text_mine = 1;
    private static final int type_text_other = 2;
    private static final int type_image = 3;
    private static final int type_image_other =4;

    private final Context context;
    private List<ChatItemModel> data;
    private boolean isSearch = false;

    public ChatAdapter(Context context,boolean isSearch) {
        this.data = new ArrayList<>();
        this.context = context;
        this.isSearch = isSearch;
    }

    public void setData(List<ChatItemModel> temp) {
        if(data != null)
            data.clear();
        this.data.addAll(temp);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case type_text_mine:
                view = LayoutInflater.from(context).inflate(R.layout.component_text_mine,parent,false);
                return new TextMineHolder(view);
            case type_text_other:
                view = LayoutInflater.from(context).inflate(R.layout.component_text_others,parent,false);
                return new TextOthersViewHolder(view);
            case type_image:
                view = LayoutInflater.from(context).inflate(R.layout.component_mine_image,parent,false);
                return new MineImageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatItemModel chatItemModel = data.get(position);
        if(holder instanceof TextMineHolder){
            setTextMine(holder,chatItemModel);
        }
        else if(holder instanceof TextOthersViewHolder)
            setOtherText(holder,chatItemModel);
        else if(holder instanceof MineImageViewHolder)
            setMineImageData(holder,chatItemModel);


        if(!isSearch)
            holder.itemView.setOnClickListener(view -> showMessageDialog(chatItemModel,position));
    }

    private void setMineImageData(RecyclerView.ViewHolder viewHolder, ChatItemModel chatItemModel) {

        MineImageViewHolder hld = (MineImageViewHolder) viewHolder;
        final Bitmap bitmap = Utils.base64Tobitmap(chatItemModel.getMessage());

        if(bitmap == null)
            return;

        final int h = (int) (bitmap.getHeight() * 0.4);
        final int w = (int) (bitmap.getWidth() * 0.4);

        hld.imageView.getLayoutParams().height = h;
        hld.imageView.getLayoutParams().width = w;

        Glide.with(context).asBitmap().load(bitmap).into(hld.imageView);


        if(chatItemModel.isSelected())
            hld.selectImage.setVisibility(View.VISIBLE);
        else
            hld.selectImage.setVisibility(View.GONE);

    }

    private void setTextMine(RecyclerView.ViewHolder holder,ChatItemModel message) {
        final TextMineHolder holder1 = (TextMineHolder) holder;
        holder1.message.setText(message.getMessage());
        final String s = Utils.convertDate(message.getTime());
        holder1.time.setText(s);

        if(message.isSelected())
            holder1.selectImage.setVisibility(View.VISIBLE);
        else
            holder1.selectImage.setVisibility(View.GONE);

    }


    private void setOtherText(RecyclerView.ViewHolder holder,ChatItemModel message) {
        final TextOthersViewHolder holder1 = (TextOthersViewHolder) holder;
        holder1.message.setText(message.getMessage());
        final String s = Utils.convertDate(message.getTime());
        holder1.time.setText(s);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        final ChatItemModel chatItemModel = data.get(position);
        if(chatItemModel.getMessageType() == _MESSAGE_TYPE_TEXT){
            if(chatItemModel.isMine())
                return type_text_mine;
            else
                return type_text_other;
        }
        else{
            if(chatItemModel.isMine())
                return type_image;
            else
                return type_image_other;
        }
    }

    class TextMineHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView message,time;
        ImageView selectImage;
        public TextMineHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message_text);
            time = itemView.findViewById(R.id.tv_time_text);
            selectImage = itemView.findViewById(R.id.select_image);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if(!isSearch) {
                contextMenu.add(getAdapterPosition(), view.getId(), 1, "Edit Message");
                contextMenu.add(getAdapterPosition(), view.getId(), 2, "Select Message");
            }
        }
    }

    class TextOthersViewHolder extends RecyclerView.ViewHolder{
        TextView message,time;
        public TextOthersViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message_text);
            time = itemView.findViewById(R.id.tv_time_text);
        }
    }

    class MineImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView imageView,selectImage;
        public MineImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.min_image);
            selectImage = itemView.findViewById(R.id.select_image);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if(!isSearch)
                contextMenu.add(getAdapterPosition(),view.getId(),2,"Select Message");
        }
    }

    private void showMessageDialog(ChatItemModel itemModel,int pos){

        final boolean isSelectedOn = ((ChatActivity)context).isSelectedOn;
        if(isSelectedOn){
            selectItem(pos);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.component_delete_dialog,null,false);

       ImageView imageView = v.findViewById(R.id.dialog_image);
       TextView textView = v.findViewById(R.id.text_view);
       
       builder.setView(v);


       if(itemModel != null && itemModel.getMessageType() == Constance._MESSAGE_TYPE_IMAGES){
           String img = itemModel.getMessage();
           if(img != null){
               final Bitmap bitmap = Utils.base64Tobitmap(img);
               imageView.setImageBitmap(bitmap);
               imageView.setVisibility(View.VISIBLE);
           }
       }
       else if(itemModel != null && itemModel.getMessageType() == _MESSAGE_TYPE_TEXT){
           textView.setText(itemModel.getMessage());
           textView.setVisibility(View.VISIBLE);
       }
        Dialog dialog = builder.create();
        
        dialog.show();
        v.findViewById(R.id.tv_share).setOnClickListener(view -> {
            shareMessage(itemModel);
            if(dialog != null)
                dialog.dismiss();
        });

        v.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null)
                    dialog.dismiss();
            }
        });
    }

    private void shareMessage(ChatItemModel item) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        if(item.getMessageType() == _MESSAGE_TYPE_TEXT)
            sendIntent.putExtra(Intent.EXTRA_TEXT, item.getMessage());
        else
        {
            final Uri bitmapUrlForShare = Utils.getBitmapUrlForShare(context, item.getMessage());
            if(bitmapUrlForShare == null)
                return;
            sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUrlForShare);

        }

        sendIntent.setType( item.getMessageType() == _MESSAGE_TYPE_TEXT ? "text/plain" : "image/jpeg");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
       context.startActivity(shareIntent);


    }

//    private void deleteChat(ChatItemModel itemModel,int pos) {
//        data.remove(pos);
//        List<ChatItemModel> temp = new ArrayList<>();
//        temp.add(itemModel);
//        DatabaseService.getInstance(context)
//                .deleteChat(temp,null);
//        notifyItemRemoved(pos);
//    }

    public void selectItem(int pos){
        data.get(pos).setSelected(!data.get(pos).isSelected());
         notifyItemChanged(pos);
        ((ChatActivity)context).selectedList.add( data.get(pos));
        ((ChatActivity)context).animateView(0);
    }

    public void resetData(){

        for(ChatItemModel item : data){
            item.setSelected(false);
        }

        notifyDataSetChanged();
    }

}
