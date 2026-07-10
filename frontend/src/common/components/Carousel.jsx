import { Carousel } from "react-bootstrap";

function BannerCarousel() {
  const banners = [
    "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=1600",
    "https://images.unsplash.com/photo-1577234286642-fc512a5f8f11?w=1600",
    "https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=1600",
  ];

  return (
    <Carousel fade interval={3000}>
      {banners.map((img, index) => (
        <Carousel.Item key={index}>
          <img
            src={img}
            alt={`banner-${index}`}
            className="d-block w-100"
            style={{
              height: "500px",
              objectFit: "cover",
            }}
          />
        </Carousel.Item>
      ))}
    </Carousel>
  );
}

export default BannerCarousel;