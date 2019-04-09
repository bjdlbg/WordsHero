package com.jeremy.wordshero.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.provider.CreatePDFWordsAdapter;
import com.jeremy.wordshero.provider.SelectWords;

import java.util.ArrayList;
import java.util.List;

public class CreatePDFActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private CreatePDFWordsAdapter adapter;
    private EditText pdfTitle, pdfInstruction;
    private Button previewPDF, createPDF;
    private List<SelectWords> selectWordsInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);
        initList(selectWordsInfoList);
        initView();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CreatePDFWordsAdapter(this, selectWordsInfoList);
        mRecyclerView.setAdapter(adapter);
        //添加分割线和默认动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //如果每个item的高度是一定的，那么这个选项可以提高性能.
        mRecyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new CreatePDFWordsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.removeItem(position);
            }
        });
    }

    public void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.create_pdf_words_recyclerView);
        pdfTitle = (EditText) findViewById(R.id.pdf_title_value);
        pdfInstruction = (EditText) findViewById(R.id.pdf_instructions_value);
        previewPDF = (Button) findViewById(R.id.preview_pdf_btn);
        createPDF = (Button) findViewById(R.id.create_pdf_btn);

        previewPDF.setOnClickListener(this);
        createPDF.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_pdf_btn:
                break;
            case R.id.create_pdf_btn:
                break;
            default:
        }
    }

    public void initList(List<SelectWords> selectWordsInfoList) {
        selectWordsInfoList.add(new SelectWords("Mechanism"));
        selectWordsInfoList.add(new SelectWords("Pastime"));
        selectWordsInfoList.add(new SelectWords("Pledge"));
        selectWordsInfoList.add(new SelectWords("Reflection"));
        selectWordsInfoList.add(new SelectWords("Revenue"));
        selectWordsInfoList.add(new SelectWords("Shortage"));
        selectWordsInfoList.add(new SelectWords("Mechanism"));
        selectWordsInfoList.add(new SelectWords("Pastime"));
        selectWordsInfoList.add(new SelectWords("Pledge"));
        selectWordsInfoList.add(new SelectWords("Reflection"));
        selectWordsInfoList.add(new SelectWords("Revenue"));
        selectWordsInfoList.add(new SelectWords("Shortage"));
    }
}
