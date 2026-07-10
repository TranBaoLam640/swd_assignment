import { Form, Badge, NavDropdown} from "react-bootstrap";
function Header({
  search,
  categories,
  selectedCategory,
  totalQuantity,
  onSearchChange,
  onCategoryChange,
}) {
  return (
    <>
      <Form.Control
        value={search}
        onChange={(e) =>
          onSearchChange(e.target.value)
        }
      />

      {categories.map((cat) => (
        <NavDropdown.Item
          key={cat.slug}
          active={
            selectedCategory === cat.slug
          }
          onClick={() =>
            onCategoryChange(cat.slug)
          }
        >
          {cat.name}
        </NavDropdown.Item>
      ))}

      <Badge>
        {totalQuantity}
      </Badge>
    </>
  );
}

export default Header;