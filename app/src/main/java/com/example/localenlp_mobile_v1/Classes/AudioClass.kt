package com.example.localenlp_mobile_v1.Classes

class AudioClass {
    var filename: String
    var filepath: String
    var timestamp: Long
    var duration: String
    var ampsPath: String

    // Constructor
    constructor(filename: String, filepath: String, timestamp: Long, duration: String, ampsPath: String) {
        this.filename = filename
        this.filepath = filepath
        this.timestamp = timestamp
        this.duration = duration
        this.ampsPath = ampsPath
    }
}
