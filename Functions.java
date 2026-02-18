class Functions {
    private final int LOWEST = 10;
    private final int HIGHEST = 100;
    int add(int a, int b) {
        if (a < LOWEST || b > HIGHEST) {
            System.out.println("Input values are outside allowed range.");
        }
        return a + b;
    }
}
