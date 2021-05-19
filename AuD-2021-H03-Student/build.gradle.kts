plugins {
  java
  application
}

// do not change assignmentId
val assignmentId: String by extra("H03")
val studentId: String by extra("lp78horo") // TU-ID  z.B. ab12cdef
val firstName: String by extra("Leon")
val lastName: String by extra("Pradler")

// !! Achtung !!
// Die studentId (TU-ID) ist keine Matrikelnummer
// Richtig z.B. ab12cdef
// Falsch z.B. 1234567

repositories {
  mavenCentral()
}

dependencies {
  // JUnit only available in "test" source set (./src/test)
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

application {
  mainClass.set("h03.Main")
}

tasks {
  create<Jar>("prepareSubmission") {
    dependsOn(test) // run tests before creating submission
    doFirst {
      if (studentId == "_not_set_" || firstName == "_not_set_" || lastName == "_not_set_") {
        throw GradleException("studentId or firstName or lastName not set!")
      }
    }
    // include source files in output jar
    from(sourceSets.main.get().allSource, sourceSets.test.get().allSource)
    // replace placeholders in resource directory
    // e.g. the submission-info.json file
    filesMatching("submission-info.json") {
      expand(project.properties)
    }
    // set the name of the output jar
    archiveFileName.set("$assignmentId-$lastName-$firstName-submission.jar")
  }
  test {
    useJUnitPlatform()
  }
}
