# Varnum

https://github.com/wehjin/varnum

## Overview

Varnum is an annotation processor that generates tagged-union classes, also known as variant-enums or sum-types, for Kotlin and Java.

An annotated interface specifies the name of the tagged-union along with the names and data-types of its members. The interface begets a tagged-union class, construction functions for each member of the union, and a match function that identifies a member and unwraps its data.

Varnum is 100% generated code with no reflection or byte-code manipulation. You can see the generated classes yourself by viewing the auto-generated tagged-union class after a build.
 
## Usage
 
Varnum works by building a class for each interface annotated with `@TaggedUnion`, e.g.

``` java
@TaggedUnion("Message")
interface MessageSpec {
    void Reset();
    void SetSize(Integer size);
    void Multi(Integer first, String second);
}
```

This example generates a tagged-union class named `Message` which has code to create an instance of each of its members: `Reset`, `SetSize`, and `Multi`.  Additional code matches an instance to a member and unwraps the member's data.

Construction takes place like this:

``` java
final Message message = Message.SetSize(5);  // Construct a message
```
 
Later, you can match a specific message and unwrap its data with `Message.match`

``` java
// Match and unwrap message
Message.match(message)
    .isSetSize(new MatchAction1<Integer>() {
        @Override
        public void call(Integer value) {
            Log.d("Matched SetSize: " + value);
        }
    })
    .isReset(new MatchAction1() {
        @Override
        public void call() {
            Log.d("Matched Reset");
        }
    })
    .orElse(new MatchAction0() {
        @Override
        public void call() {
            Log.d("Matched wildcard");
        }
    });
```

Tagged unions are particularly useful for declaring the inputs that an actor or thread can handle.  To communicate with the actor, one need only create a message that conforms to the actor's input type and add the message to the actor's message queue or deliver the message to a stream on which the actor has subscribed an observer (Rx).

## Download (Java)

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.wehjin.varnum:varnum:v1.1'
    apt 'com.github.wehjin.varnum:varnum-compiler:v1.1'
}
```
 
## License
    Copyright 2016 Jeffrey Yu.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.