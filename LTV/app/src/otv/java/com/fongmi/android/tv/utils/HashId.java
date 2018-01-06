package com.fongmi.android.tv.utils;

public class HashId {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private int minHashLength;
    private String alphabet;
    private String guards;
    private String salt;
    private String sep;

    public HashId(String salt) {
        this(salt, 0);
    }

    private HashId(String salt, int minHashLength) {
        this(salt, minHashLength, ALPHABET);
    }

    private HashId(String salt, int minHashLength, String alphabet) {
        int i;
        this.salt = "";
        this.alphabet = "";
        this.sep = "cfhistuCFHISTU";
        this.minHashLength = 0;
        this.salt = salt;
        if (minHashLength < 0) {
            this.minHashLength = 0;
        } else {
            this.minHashLength = minHashLength;
        }
        this.alphabet = alphabet;
        String uniqueAlphabet = "";
        for (i = 0; i < this.alphabet.length(); i++) {
            if (!uniqueAlphabet.contains("" + this.alphabet.charAt(i))) {
                uniqueAlphabet = uniqueAlphabet + "" + this.alphabet.charAt(i);
            }
        }
        this.alphabet = uniqueAlphabet;
        if (this.alphabet.length() < 16) {
            throw new IllegalArgumentException("alphabet must contain at least " + 16 + " unique characters");
        } else if (this.alphabet.contains(" ")) {
            throw new IllegalArgumentException("alphabet cannot contains spaces");
        } else {
            for (i = 0; i < this.sep.length(); i++) {
                int j = this.alphabet.indexOf(this.sep.charAt(i));
                if (j == -1) {
                    this.sep = this.sep.substring(0, i) + " " + this.sep.substring(i + 1);
                } else {
                    this.alphabet = this.alphabet.substring(0, j) + " " + this.alphabet.substring(j + 1);
                }
            }
            this.alphabet = this.alphabet.replaceAll("\\s+", "");
            this.sep = this.sep.replaceAll("\\s+", "");
            this.sep = consistentShuffle(this.sep, this.salt);
            if (this.sep.equals("") || ((double) (this.alphabet.length() / this.sep.length())) > 3.5d) {
                int sep_len = (int) Math.ceil(((double) this.alphabet.length()) / 3.5d);
                if (sep_len == 1) {
                    sep_len++;
                }
                if (sep_len > this.sep.length()) {
                    int diff = sep_len - this.sep.length();
                    this.sep += this.alphabet.substring(0, diff);
                    this.alphabet = this.alphabet.substring(diff);
                } else {
                    this.sep = this.sep.substring(0, sep_len);
                }
            }
            this.alphabet = consistentShuffle(this.alphabet, this.salt);
            int guardCount = (int) Math.ceil(((double) this.alphabet.length()) / ((double) 12));
            if (this.alphabet.length() < 3) {
                this.guards = this.sep.substring(0, guardCount);
                this.sep = this.sep.substring(guardCount);
                return;
            }
            this.guards = this.alphabet.substring(0, guardCount);
            this.alphabet = this.alphabet.substring(guardCount);
        }
    }

    public String encode(long... numbers) {
        for (long number : numbers) {
            if (number > 9007199254740992L) {
                throw new IllegalArgumentException("number can not be greater than 9007199254740992L");
            }
        }
        return numbers.length == 0 ? "" : _encode(numbers);
    }

    private String _encode(long... numbers) {
        int i;
        int numberHashInt = 0;
        for (i = 0; i < numbers.length; i++) {
            numberHashInt = (int) (((long) numberHashInt) + (numbers[i] % ((long) (i + 100))));
        }
        String alphabet = this.alphabet;
        char ret = alphabet.toCharArray()[numberHashInt % alphabet.length()];
        String ret_str = ret + "";
        for (i = 0; i < numbers.length; i++) {
            long num = numbers[i];
            alphabet = consistentShuffle(alphabet, (ret + this.salt + alphabet).substring(0, alphabet.length()));
            String last = hash(num, alphabet);
            ret_str = ret_str + last;
            if (i + 1 < numbers.length) {
                ret_str = ret_str + this.sep.toCharArray()[(int) ((num % ((long) (last.toCharArray()[0] + i))) % ((long) this.sep.length()))];
            }
        }
        if (ret_str.length() < this.minHashLength) {
            ret_str = this.guards.toCharArray()[(ret_str.toCharArray()[0] + numberHashInt) % this.guards.length()] + ret_str;
            if (ret_str.length() < this.minHashLength) {
                ret_str = ret_str + this.guards.toCharArray()[(ret_str.toCharArray()[2] + numberHashInt) % this.guards.length()];
            }
        }
        int halfLen = alphabet.length() / 2;
        while (ret_str.length() < this.minHashLength) {
            alphabet = consistentShuffle(alphabet, alphabet);
            ret_str = alphabet.substring(halfLen) + ret_str + alphabet.substring(0, halfLen);
            int excess = ret_str.length() - this.minHashLength;
            if (excess > 0) {
                int start_pos = excess / 2;
                ret_str = ret_str.substring(start_pos, this.minHashLength + start_pos);
            }
        }
        return ret_str;
    }

    private String consistentShuffle(String alphabet, String salt) {
        if (salt.length() <= 0) {
            return alphabet;
        }
        char[] arr = salt.toCharArray();
        int i = alphabet.length() - 1;
        int v = 0;
        int p = 0;
        while (i > 0) {
            v %= salt.length();
            int asc_val = arr[v];
            p += asc_val;
            int j = ((asc_val + v) + p) % i;
            char tmp = alphabet.charAt(j);
            alphabet = alphabet.substring(0, j) + alphabet.charAt(i) + alphabet.substring(j + 1);
            alphabet = alphabet.substring(0, i) + tmp + alphabet.substring(i + 1);
            i--;
            v++;
        }
        return alphabet;
    }

    private String hash(long input, String alphabet) {
        String hash = "";
        int alphabetLen = alphabet.length();
        char[] arr = alphabet.toCharArray();
        do {
            hash = arr[(int) (input % ((long) alphabetLen))] + hash;
            input /= (long) alphabetLen;
        } while (input > 0);
        return hash;
    }
}
