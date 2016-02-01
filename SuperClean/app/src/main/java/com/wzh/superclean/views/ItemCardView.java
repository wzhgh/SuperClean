package com.wzh.superclean.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzh.superclean.R;

/**
 * Created by wzh on 2015/9/14.
 */
public class ItemCardView extends RelativeLayout {

    private Context mContext ;
    private ImageView mImageView ;
    private TextView mCardName ;

    /**
     * 既然要使用自定义的属性资源，需要使用AttributeSet,那么不要使用这个构造器
     * 而是使用public ItemCardView(Context context, AttributeSet attrs) 这个构造器
     */
//    public ItemCardView(Context context) {
//        super(context);
//        mContext = context ;
//        initView() ;
//    }

    public ItemCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context ;
        initView() ;
        //这个构造器有AttributeSet  ，需要在这个构造器里引用自定义的属性资源
        //使用自定义的属性资源R.styleable.ItemCardView
        TypedArray tArray = context.obtainStyledAttributes(attrs,R.styleable.ItemCardView) ;
        Drawable imageDrawer = tArray.getDrawable(R.styleable.ItemCardView_card_img) ;
        String cardName = tArray.getString(R.styleable.ItemCardView_card_name) ;
        mImageView.setImageDrawable(imageDrawer);
        mCardName.setText(cardName);
        //Recycle the TypedArray, to be re-used by a later caller
        tArray.recycle();
    }

    private void initView(){
        //填充布局文件，获取布局文件的View对象，从而获取相应的控件
        LayoutInflater lInflater = LayoutInflater.from(mContext) ;
        View mView = lInflater.inflate(R.layout.item_card_layout, this) ;
        mImageView = (ImageView)mView.findViewById(R.id.card_img_id) ;
        mCardName = (TextView)mView.findViewById(R.id.card_name_id) ;
    }
}
