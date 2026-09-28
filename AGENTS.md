# imagedecoder

```
stack   JDK21 · Maven3.9+ · Boot 4.1.1 · MPL-2.0 · parent imagedecoder-parent
prereq  ../commons/kernel → kernel-core, version = kernel.core.version in pom.xml (Logfactory lives here)
reactor
├─ imagedecoder/   published lib: JPEG2000 + WSQ
└─ sample/         CLI demo, NOT in reactor
pkg io/mosip/imagedecoder/
├─ spi/        IImageDecoderApi
├─ openjpeg/   OpenJpegDecoder + *Helper (OpenJPEG 1.x C port)
├─ wsq/        WsqDecoder + *Helper (NBIS port)
├─ model/      req/resp + openjpeg/ wsq/ C-struct mirrors
├─ util/       Base64UrlUtil, ByteStreamUtil, wsq/WsqUtil
└─ constant/ exceptions/ logger/
api     Response<DecoderResponseInfo> decode(DecoderRequestInfo) → .jp2 OpenJpeg | .wsq Wsq
rules
├─ ban     kernel-bom · kernel-logger-logback · Jackson3 (use spring-boot-jackson2)
├─ lib     no Boot repackage
├─ cover   jacoco ≥0.85 instr · Sonar = (line+branch) ≥85% · excl config/dto/entity ONLY
├─ tests   write tests, never add exclusions
└─ license approved: MIT/BSD/Apache/MPL/ISC/CDDL/Zlib/0BSD · no EPL/GPL/AGPL/LGPL/CC at runtime
   └─ on dep change: regen report → update NOTICE, THIRD-PARTY-NOTICES, licenses/
gotchas
├─ Cio/Bio/MQC: array+index "pointers"; byteOut PRE-increments → next byte at bpIndex+1
├─ Cio: start=bpIndex=-1, end=start+length
├─ Lombok @Data on cyclic models → @ToString.Exclude/@EqualsAndHashCode.Exclude
├─ new X[n] of models → init every element (NPE otherwise)
├─ JP2 box readers must bound length (else hang on corrupt input)
└─ corrupt-input tests log millions of lines → read only `Get-Content log -Tail`
cmds (PowerShell: quote -D args)
├─ root    mvn verify "-Dgpg.skip=true"                 # full build + jacoco gate
├─ module  mvn -q test "-Dtest=Class#method" "-Dgpg.skip=true"
├─ sonar   mvn verify sonar:sonar -Psonar
├─ cov     parse imagedecoder/target/site/jacoco/jacoco.xml (LINE+BRANCH counters)
├─ lic     (in imagedecoder/) mvn org.codehaus.mojo:license-maven-plugin:2.7.1:add-third-party "-Dlicense.includedScopes=compile,runtime"
└─ local   imagedecoder/run-local.(bat|sh) init|test|all
```
