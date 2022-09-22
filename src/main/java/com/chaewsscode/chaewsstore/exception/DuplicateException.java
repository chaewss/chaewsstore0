package com.chaewsscode.chaewsstore.exception;

import com.chaewsscode.chaewsstore.util.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DuplicateException extends RuntimeException {

    private final ResponseCode responseCode;
}
