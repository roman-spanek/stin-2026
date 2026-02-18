class Functions {
    private final int LOWEST = 10;
    private final int HIGHEST = 100;
    int add(int a, int b) {
	if (a < LOWEST || a > HIGHEST ||
            b < LOWEST || b > HIGHEST) {
            throw new IllegalArgumentException("Input values are outside allowed range.");
            return -1;
	}
        return a + b;
    }
}
