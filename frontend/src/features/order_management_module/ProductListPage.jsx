import { useEffect, useState } from "react";
import { Container, Row, Col, Carousel } from "react-bootstrap";

import Header from "../../common/components/Header";
import ProductCard from "../../common/components/ProductCard";
import BannerCarousel from "../../common/components/Carousel";

import { getProducts } from "../../common/services/productService";
import { getCategories } from "../../common/services/categoryService";

function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("all");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const productData = await getProducts();
    const categoryData = await getCategories();

    setProducts(productData);
    setCategories(categoryData);
  };

  const filteredProducts = products.filter((product) => {
    const matchSearch = product.title
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchCategory =
      category === "all"
        ? true
        : product.category === category;

    return matchSearch && matchCategory;
  });

  return (
    <>
      <Header
        search={search}
        categories={categories}
        selectedCategory={category}
        totalQuantity={0}
        onSearchChange={setSearch}
        onCategoryChange={setCategory}
      />

      <BannerCarousel/>

      <Container className="mt-4">
        <Row className="g-4">
          {filteredProducts.map((product) => (
            <Col
              key={product.id}
              lg={3}
              md={4}
              sm={6}
            >
              <ProductCard product={product} />
            </Col>
          ))}
        </Row>
      </Container>
    </>
  );
}

export default ProductListPage;