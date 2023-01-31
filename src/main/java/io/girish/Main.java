package io.girish;

import io.girish.cli.Menu;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();

        System.out.println();

        menu.showWelcome();

        menu.getInput();
    }
}
