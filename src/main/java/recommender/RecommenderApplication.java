package recommender;

import java.net.UnknownHostException;

import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RecommenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommenderApplication.class, args);
	}

	@Bean
	MongoDBDataModel dbm() throws UnknownHostException {
		MongoDBDataModel dbm = new MongoDBDataModel("127.0.0.1", 27017, "test", "ratings", false, false, null);
		return dbm;
	}

	@Bean
	SVDRecommender recommender() throws Exception {
		MongoDBDataModel dbm = dbm();
		SVDRecommender svd = new SVDRecommender(dbm, new ALSWRFactorizer(dbm, 3, 0.05f, 50));
		return svd;
	}

	@Bean
	CachingRecommender itemRecommender() throws Exception {
		MongoDBDataModel dbm = dbm();
		TanimotoCoefficientSimilarity itemSimilarity = new TanimotoCoefficientSimilarity(dbm);
		CachingRecommender recommender = new CachingRecommender(
				new GenericBooleanPrefItemBasedRecommender(dbm, itemSimilarity));
		return recommender;
	}
}
