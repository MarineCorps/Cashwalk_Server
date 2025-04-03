-- 💣 기존 데이터 모두 삭제
DELETE FROM store_item;

-- 🛍️ 테스트용 스토어 아이템 데이터 삽입
INSERT INTO store_item (id, name, required_points, stock) VALUES
                                                              (1, '스타벅스 아메리카노', 500, 10),
                                                              (2, '배달의민족 5,000원 쿠폰', 500, 5),
                                                              (3, '편의점 3,000원 쿠폰', 300, 20);
