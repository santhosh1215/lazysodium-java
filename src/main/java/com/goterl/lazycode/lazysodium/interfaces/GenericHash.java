/*
 * Copyright (c) Terl Tech Ltd • 09/05/18 01:11 • goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.goterl.lazycode.lazysodium.interfaces;


import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.utils.BaseChecker;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public interface GenericHash {

    int SIPHASH24_BYTES = 8,
        SIPHASH24_KEYBYTES = 16,
        BLAKE2B_BYTES_MAX = 64,
        BLAKE2B_BYTES_MIN = 16,
        BLAKE2B_KEYBYTES_MIN = 16,
        BLAKE2B_KEYBYTES_MAX = 64,

        BYTES = SIPHASH24_BYTES,
        KEYBYTES = SIPHASH24_KEYBYTES,

        BYTES_MAX = BLAKE2B_BYTES_MAX,
        BYTES_MIN = BLAKE2B_BYTES_MIN,

        KEYBYTES_MIN = BLAKE2B_KEYBYTES_MIN,
        KEYBYTES_MAX = BLAKE2B_KEYBYTES_MAX;


    class Checker extends BaseChecker {

    }

    interface Native {

        /**
         * Generate a key. Store the key in {@code k}.
         * @param k A place to store the generated key
         *          of size {@link #KEYBYTES}. Though,
         *          it may be between {@link #KEYBYTES_MIN}
         *          and {@link #KEYBYTES_MAX}.
         */
        void cryptoGenericHashKeygen(byte[] k);

        /**
         * Hash a byte array.
         * @param out A place to store the resulting byte array.
         *            You may choose the output size, however the minimum
         *            recommended output size is {@link #BYTES}.
         *            You may also specify a value
         *            between {@link #BYTES_MIN} and {@link #BYTES_MAX}.
         * @param outLen Size of out.
         * @param in The text to hash.
         * @param inLen The size of in.
         * @param key The key generated by {@link #cryptoGenericHashKeygen(byte[])}
         *            of size {@link #KEYBYTES}. Though, it may be
         *            between {@link #KEYBYTES_MIN} and {@link #KEYBYTES_MAX}.
         * @param keyLen The length of the key.
         * @return True if successfully hashed.
         */
        boolean cryptoGenericHash(
                byte[] out, int outLen,
                byte[] in, long inLen,
                byte[] key, int keyLen
        );


        /**
         * Hash multiple parts of a message.
         * @param state The state which holds the key
         *              in memory for further hashing.
         * @param key The key generated by {@link #cryptoGenericHashKeygen(byte[])}.
         * @param keyLength Length of the key.
         * @param outLen The size of the hash array. Please
         *               see the param {@code out} in
         *               {@link #cryptoGenericHash(byte[], int, byte[], long, byte[], int)}
         *               for more information.
         *
         * @return True if initialised.
         */
        boolean cryptoGenericHashInit(GenericHash.State state,
                                   byte[] key,
                                   int keyLength,
                                   int outLen);

        /**
         * Update a multi-part hashing with another part.
         * @param state The state.
         * @param in Another hash part.
         * @param inLen The length if the hash part.
         * @return True if this part of the message was hashed.
         */
        boolean cryptoGenericHashUpdate(GenericHash.State state,
                                     byte[] in,
                                     long inLen);

        /**
         * Now that the hash has finalised, the hash can
         * be put into {@code out}.
         * @param state The state.
         * @param out The final hash.
         * @param outLen The length of the hash.
         * @return True if hashed.
         */
        boolean cryptoGenericHashFinal(GenericHash.State state, byte[] out, int outLen);

    }

    interface Lazy {

        /**
         * Generate a hashing key.
         * @return A hashing key.
         */
        String cryptoGenericHashKeygen();

        /**
         * Hash a string without a key.
         * @param in The string to hash.
         * @return The hashed string.
         */
        String cryptoGenericHash(String in) throws SodiumException;

        /**
         * Hash a string with a key, so later on you
         * can verify the hashed string with the key.
         * If you're hashing a password please see {@link PwHash.Lazy#cryptoPwHashStr(String, long, long)}
         * instead.
         * @param in The string to hash.
         * @param key Can be null.
         * @return A hashed string.
         */
        String cryptoGenericHash(String in, String key) throws SodiumException;

        /**
         * Initialise a multi-part hashing operation.
         * @param state The state which holds the key and operation.
         * @param key The key as generated by {@link #cryptoGenericHashKeygen()}.
         * @param outLen The size of the final hash.
         * @return True if initialised.
         */
        boolean cryptoGenericHashInit(GenericHash.State state,
                                      String key,
                                      int outLen);

        /**
         * Hash a part of a multi-part hash.
         * @param state State as put into {@link #cryptoGenericHashInit(State, String, int)}.
         * @param in A part of a string to hash.
         * @return The hashed part.
         */
        String cryptoGenericHashUpdate(GenericHash.State state, String in);

        /**
         * Finalise the hashing operation.
         * @param state State as put into {@link #cryptoGenericHashInit(State, String, int)}.
         * @param outLen The size of the final hash.
         * @return The final hash.
         */
        String cryptoGenericHashFinal(GenericHash.State state, int outLen);

    }


    class State extends Structure {

        public static class ByReference extends GenericHash.State implements Structure.ByReference { }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("h", "t", "last_node");
        }

        public byte[] h = new byte[8];
        public byte[] t = new byte[2];
        public int last_node;

    }


}
