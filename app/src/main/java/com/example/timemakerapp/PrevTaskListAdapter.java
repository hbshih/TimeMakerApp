package com.example.timemakerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.LinkedList;

public class PrevTaskListAdapter extends
        RecyclerView.Adapter<PrevTaskListAdapter.TaskViewHolder> {

    private final LinkedList<DailyTask> mPrevTaskList;
    private DailyTask currentTask;
    private LayoutInflater mInflater;
    private Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PrevTaskListAdapter(Context context, LinkedList<DailyTask> prevTaskList, DailyTask current) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mPrevTaskList = prevTaskList;
        this.currentTask = current;

        Log.d("Adapter", "task number" + mPrevTaskList.size());
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
        final DailyTask mCurrent = mPrevTaskList.get(position);
        holder.prevTaskItemView.setText(mCurrent.getName());
        final PrevTaskListAdapter mAdapter = this;

        if (currentTask != null){
            holder.checkBox.setEnabled(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.pickFocusTitle)
                            .setMessage(R.string.pickTaskConfirmation)
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

                                    dailyFocus.setText(mCurrent.getName());
                                    dailyFocus.setVisibility(View.VISIBLE);
                                    focusCheckbox.setVisibility(View.VISIBLE);

                                    mPrevTaskList.remove(mCurrent);

                                    final DailyTask newTask = new DailyTask(mCurrent.getName(), new Date(),false, mCurrent.getUserId());
                                    db.collection("tasks").add(newTask).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                DocumentReference doc = task.getResult();
                                                newTask.setId(doc.getId());
                                                mAdapter.setCurrentTask(newTask);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    db.collection("tasks").document(mCurrent.getId())
                                            .update("achieved", true);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                    holder.checkBox.setChecked(false);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_task_title)
                        .setMessage(R.string.delete_task_text)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mPrevTaskList.remove(mCurrent);
                                mAdapter.notifyDataSetChanged();

                                db.collection("tasks").document(mCurrent.getId())
                                        .update("achieved", true);

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPrevTaskList.size();
    }

    public void setCurrentTask(DailyTask newTask){
        this.currentTask = newTask;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView prevTaskItemView;
        public final CheckBox checkBox;
        public final ImageButton deleteButton;
        final PrevTaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, PrevTaskListAdapter adapter) {
            super(itemView);
            this.prevTaskItemView = itemView.findViewById(R.id.previous_task);
            this.checkBox = itemView.findViewById(R.id.prev_task_checkbox);
            this.deleteButton = itemView.findViewById(R.id.delete_button);
            this.mAdapter = adapter;
        }

    }

}
