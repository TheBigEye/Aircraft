package minicraft.screen.entry;

public class RangeEntry extends ArrayEntry<Integer> {
	
    @SuppressWarnings("unused")
    private int min, max;

    private static Integer[] getIntegerArray(int min, int max) {
        Integer[] ints = new Integer[max - min + 1];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = Integer.valueOf(min + i);
        }
        return ints;
    }

    public RangeEntry(String label, int min, int max, int initial) {
        super(label, false, getIntegerArray(min, max));

        this.min = min;
        this.max = max;

        setValue(Integer.valueOf(initial));
    }

    @Override
    public void setValue(Object object) {
        if (!(object instanceof Integer)) {
            return;
        }
        setSelection(((int) object) - min);
    }
}
