module M {
    spec module {
        pragma verify;
        pragma verify = true;
        pragma verify = true, timeout;
        pragma verify = true, timeout = 60;
        pragma friend = gg;
    }
}
