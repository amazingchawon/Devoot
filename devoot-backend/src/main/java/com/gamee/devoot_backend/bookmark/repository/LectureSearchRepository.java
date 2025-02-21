package com.gamee.devoot_backend.bookmark.repository;

// @Repository
// public interface LectureSearchRepository extends ElasticsearchRepository<LectureDocument, String> {
// 	// Basic search across name, lecturer, and tags
// 	@Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"lecturer\", \"tags\"]}}")
// 	List<LectureDocument> searchByAllFields(String searchTerm);
//
// 	// Combined search with filters
// 	@Query("{\"bool\": {" +
// 		"  \"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"lecturer\", \"tags\"]}}]," +
// 		"  \"filter\": [" +
// 		"    {\"term\": {\"categoryName\": \"?1\"}}," +
// 		"    {\"range\": {\"currentPrice\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}" +
// 		"  ]" +
// 		"}}")
// 	List<LectureDocument> searchWithFilters(
// 		String searchTerm,
// 		String categoryName,
// 		Integer minPrice,
// 		Integer maxPrice
// 	);
//
// 	// Price range query
// 	List<LectureDocument> findByCurrentPriceBetween(Integer minPrice, Integer maxPrice);
//
// 	// Category and price range
// 	List<LectureDocument> findByCategoryNameAndCurrentPriceBetween(
// 		String categoryName,
// 		Integer minPrice,
// 		Integer maxPrice
// 	);
// }
