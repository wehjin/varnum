# Varnum

https://github.com/wehjin/varnum

## Overview

Varnum is an annotation processor that generates tagged-union classes, also known as variant-enums or sum-types, for Kotlin and Java.

An annotated interface specifies the name of the tagged-union along with the names and data-types of its members. The interface begets a tagged-union class, construction functions for each member of the union, and a match function that identifies a member and unwraps its data.

Varnum is 100% generated code with no reflection or byte-code manipulation. You can see the generated classes yourself by viewing the auto-generated tagged-union class after a build.
 
## Usage
 
Varnum works by building a class for each interface annotated with `@TaggedUnion`, e.g.

```
@TaggedUnion("Message")
interface MessageSpec {
    void Reset();
    void SetSize(Integer size);
    void Multi(Integer first, String second);
}
```

This example generates a class named `Message` (the name is specified in the annotation) which has the code to create a message instance with associated data for each of its members, `Reset`, `SetSize`, and `Multi`.  Additional code matches a particular member and unwraps its associated data.

Construction takes place like this:

```
final Message message = Message.SetSize(5);  // Construct a message
queue.push(message);  // Pass message to another actor
```
 
Later, match specific messages and unwrap associated data with `Message.match`

```
final Message message = queue.pop();   // Retrieve message

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