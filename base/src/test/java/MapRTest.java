
import com.novelbio.base.PathDetail;
import com.novelbio.base.dataOperate.TxtReadandWrite;
import com.novelbio.base.fileOperate.FileHadoop;

/**
 * Assumes mapr installed in /opt/mapr
 * 
 * compilation needs only hadoop jars: javac -cp
 * /opt/mapr/hadoop/hadoop-0.20.2/lib/hadoop-0.20.2-dev-core.jar MapRTest.java
 * 
 * Run: java -Djava.library.path=/opt/mapr/lib -cp
 * /opt/mapr/hadoop/hadoop-0.20.2
 * /conf:/opt/mapr/hadoop/hadoop-0.20.2/lib/hadoop-
 * 0.20.2-dev-core.jar:/opt/mapr/
 * hadoop/hadoop-0.20.2/lib/maprfs-0.1.jar:.:/opt/mapr
 * /hadoop/hadoop-0.20.2/lib/commons
 * -logging-1.0.4.jar:/opt/mapr/hadoop/hadoop-0.20.2/lib/zookeeper-3.3.2.jar
 * MapRTest /test
 */
public class MapRTest {
	public static void main(String args[]) throws Exception {
		byte buf[] = new byte[65 * 1024];
		int ac = 0;

		// maprfs:/// -> uses the first entry in
		// /opt/mapr/conf/mapr-clusters.conf
		// maprfs:///mapr/my.cluster.com/
		// /mapr/my.cluster.com/
//		FileHadoop fileHadoop = new FileHadoop("/haha/hoo");
//		fileHadoop.mkdirs();
		String dirname = "/my.cluster.com/hoho";
//		
//		Configuration conf = new Configuration();
//		conf.set("dfs.permissions", "false");
		FileHadoop fileHadoop = new FileHadoop(PathDetail.getHdfsHeadSymbol() + "/test/fastq/798B_CGATGT_L004_R1_001.fastq.gz");
		System.out.println(fileHadoop.getName());
		System.out.println(fileHadoop.getAbsolutePath());
		System.out.println(fileHadoop.getParent());
		FileHadoop fileHadoop2 = new FileHadoop(fileHadoop.getParent());
		
		TxtReadandWrite txtRead = new TxtReadandWrite(PathDetail.addHdfsHeadSymbol("/test/fastq/798B_CGATGT_L004_R1_001.fastq.gz"));
		int i = 0;
		for (String content : txtRead.readlines()) {
			if (i > 10) {
				break;
			}
			i++;
			System.out.println(content);
		}
		
//		FileSystem fs = HdfsBase.getFileSystem();
//		fs.mkdirs(new Path(dirname));
		//FileSystem fs = FileSystem.get(URI.create(uri), conf); // if wanting
//		// to use a different cluster
//		//FileSystem fs = FileSystem.get(conf);
//
		//Path dirpath = new Path(dirname + "/dir");
//		// Path rfilepath = new Path( dirname + "/file.r");
//		System.out.println(dirpath.getParent());
//		// try mkdir
		//boolean res = fs.mkdirs(dirpath);
//		if (!res) {
//			System.out.println("mkdir failed, path: " + dirpath);
//			return;
//		}
//
//		System.out.println("mkdir( " + dirpath + ") went ok, now writing file");
//
//		// create wfile
//		FSDataOutputStream ostr = fs.create(wfilepath, true, // overwrite
//				512, // buffersize
//				(short) 1, // replication
//				(long) (64 * 1024 * 1024) // chunksize
//				);
//		ostr.write(buf);
//		ostr.close();
//
//		System.out.println("write( " + wfilepath + ") went ok");
//
//		// read rfile
//		System.out.println("reading file: " + rfilepath);
//		FSDataInputStream istr = fs.open(rfilepath);
//		int bb = istr.readInt();
//		istr.close();
//		System.out.println("Read ok");
	}
}