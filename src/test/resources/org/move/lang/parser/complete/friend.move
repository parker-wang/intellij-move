module M1 {
    friend 0x1::M2;
    friend 0x2::M2;

    public(friend) fun f_of_m1() {}
}
