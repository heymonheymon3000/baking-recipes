package baking.nanodegree.android.baking.utilities;

public class Events {
    public static class FragmentActivityMessage {
        private int currentStepIndex;

        public FragmentActivityMessage(int currentStepIndex) {
            this.currentStepIndex = currentStepIndex;
        }
        public int getcurrentStepIndex() {
            return currentStepIndex;
        }
    }

    public static class ActivityFragmentMessage {
        private int currentStepIndex;
        public ActivityFragmentMessage(int currentStepIndex) {
            this.currentStepIndex = currentStepIndex;
        }
        public int getcurrentStepIndex() {
            return currentStepIndex;
        }
    }

    public static class ActivityActivityMessage {
        private int currentStepIndex;
        public ActivityActivityMessage(int currentStepIndex) {
            this.currentStepIndex = currentStepIndex;
        }
        public int getcurrentStepIndex() {
            return currentStepIndex;
        }
    }
}
