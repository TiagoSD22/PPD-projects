package com.spatia.client.app.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Avatars {
    public static final List<String> avatarNames = Stream.iterate(1, n -> n + 1).limit(50)
            .map(n -> n.toString().concat(".png")).collect(Collectors.toList());

}
