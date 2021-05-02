package cn.insinuate.loader;

import java.util.Arrays;
import java.util.List;

import cn.insinuate.loader.maven.MavenArtifact;
import cn.insinuate.loader.utils.Pair;

public class InsinuateLibraries {

    public static List<MavenArtifact> artifacts = Arrays.asList(
            new MavenArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.5.0", Arrays.asList(
                    new Pair<>("kotlin", "cn.insinuate.libs.kotlin")
            )),
            new MavenArtifact("commons-io:commons-io:2.8.0", Arrays.asList(
                    new Pair<>("org.apache", "cn.insinuate.libs.org.apache")
            ))
    );

}
