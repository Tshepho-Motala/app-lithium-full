package lithium.metrics;

import java.text.NumberFormat;
import java.util.ArrayList;

public class StopWatch extends org.springframework.util.StopWatch {

    private StopWatch parent;
    private ArrayList<StopWatch> children = new ArrayList<>();
    private StopWatch runningChild = null;

    public StopWatch(String id) {
        super(id);
    }

    public StopWatch(String id, StopWatch parent) {
        super((parent != null) ? parent.getId() + "." + id : id);
        if (parent == null) return;
        this.parent = parent;
        parent.addChild(this);
    }

    @Override
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        if (parent == null) {
            sb.append(shortSummary());
            sb.append('\n');
            sb.append("-----------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("-----------------------------------------\n");
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(5);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (TaskInfo task : getTaskInfo()) {
            sb.append(nf.format(task.getTimeMillis())).append("  ");
            sb.append(pf.format(task.getTimeSeconds() / getTotalTimeSeconds())).append("  ");
            sb.append(task.getTaskName()).append("\n");
        }

        for (StopWatch child : children) {
            sb.append(child.prettyPrint());
        }
        return sb.toString();
    }

    @Override
    public void start(String taskName) throws IllegalStateException {
        if (isRunning()) {
            if (runningChild == null) {
                runningChild = new StopWatch(taskName, this);
                runningChild.start(taskName);
            }
        } else {
            super.start((parent != null) ? parent.getId() + "." + taskName : taskName);
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (runningChild != null) {
            runningChild.stop();
            runningChild = null;
        } else {
            super.stop();
        }
    }

    public void addChild(StopWatch child) {
        children.add(child);
    }

    public void removeChild(StopWatch child) {
        children.remove(child);
    }
}
