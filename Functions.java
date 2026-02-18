class Functions {
    private final int LOWEST = 10;
    private final int HIGHEST = 100;
    int add(int a, int b) {
	if (a < LOWEST || a > HIGHEST ||
            b < LOWEST || b > HIGHEST) {
            System.out.println("Values must be between 10 and 100");
            return -1;
	}
        return a + b;
    }
}
