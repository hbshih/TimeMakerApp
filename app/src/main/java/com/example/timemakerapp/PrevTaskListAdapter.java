package com.example.timemakerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PrevTaskListAdapter extends
        RecyclerView.Adapter<PrevTaskListAdapter.TaskViewHolder>   {

    private final LinkedList<String> mPrevTaskList;
    private LayoutInflater mInflater;
    private Context context;

    public PrevTaskListAdapter(Context context,LinkedList<String> prevTaskList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mPrevTaskList = prevTaskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mTaskView = mInflater.inflate(R.layout.previous_task_item,
                parent, false);
        return new TaskViewHolder(mTaskView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull final PrevTaskListAdapter.TaskViewHolder holder, int position) {
        final String mCurrent = mPrevTaskList.get(position);
        holder.prevTaskItemView.setText(mCurrent);
        final PrevTaskListAdapter mAdapter = this;

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    new AlertDialog.Builder(context)
                            .setTitle("New Task")
                            .setMessage(R.string.pickOldTaskConfirmation)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    LinearLayout view = (LinearLayout) buttonView.getParent().getParent().getParent().getParent().getParent();
                                    TextView dailyFocus = view.findViewById(R.id.daily_focus_text);
                                    CheckBox focusCheckbox = view.findViewById(R.id.checkBox);
                                    EditText enterFocus = view.findViewById(R.id.editText);
                                    Button focusButton = view.findViewById(R.id.button);

                                    enterFocus.setVisibility(View.INVISIBLE);
                                    focusButton.setVisibility(View.INVISIBLE);

                                    holder.checkBox.setEnabled(false);

                                    dailyFocus.setText(mCurrent);
                                    dailyFocus.setVisibility(View.VISIBLE);
                                    focusCheckbox.setVisibility(View.VISIBLE);

                                    mPrevTaskList.remove(mCurrent);
                                    mAdapter.notifyDataSetChanged();

                                    //TODO: Write current task to database

                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                    holder.checkBox.setChecked(false);
                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPrevTaskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView prevTaskItemView;
        public final CheckBox checkBox;
        final PrevTaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, PrevTaskListAdapter adapter){
            super(itemView);
            this.prevTaskItemView = itemView.findViewById(R.id.previous_task);
            this.checkBox = itemView.findViewById(R.id.prev_task_checkbox);
            this.mAdapter = adapter;
        }

    }

}
