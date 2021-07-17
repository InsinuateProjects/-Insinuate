package io.insinuate.core

import java.io.File

class InsinuateLoader(
    val bootClassLoader: ClassLoader,
    val insinuateFolder: File
)