package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;

import java.nio.ByteBuffer;

public interface Sendable {
    ByteBuffer serialize();
    CommandMap getMessageName();
}
