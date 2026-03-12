INSERT INTO request (created_by_id, status_id, origin_id, destination_id, loading_address, loading_lat, loading_lon,
                     unloading_address, unloading_lat, unloading_lon, cargo_type_id, cargo_weight, cargo_volume,
                     cargo_description, customer_id, request_number, planned_loading_at, planned_unloading_at)
VALUES
    (1, 1, 1, 2, 'Склад №1, Москва, ул. Ленина, 10', 55.7054, 37.7258, 'Склад №3, Санкт-Петербург, пр. Невский, 25',
     59.7881, 30.4182, 1, 1200.5, 15.2, 'Партия электроники: ноутбуки и телефоны', 1, 'REQ001',
     CURRENT_TIMESTAMP + interval '1 day' + interval '9 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '18 hour'),

    (1, 3, 6, 5, 'Терминал №2, Казань, ул. Победы, 8', 55.7485, 49.1021, 'Терминал №1, Нижний Новгород, ул. Лесная, 18',
     56.2814, 43.8910, 2, 5400.0, 42.0, 'Цемент и кирпич для строительной компании', 2, 'REQ002',
     CURRENT_TIMESTAMP + interval '2 day' + interval '10 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '12 hour'),

    (1, 1, 4, 13, 'Завод "МеталлПром", Екатеринбург', 56.7312, 60.7435, 'Склад "ПермТорг", Пермь',
     57.9488, 56.1892, 5, 3500.0, 28.5, 'Металлические детали и запчасти', 4, 'REQ003',
     CURRENT_TIMESTAMP + interval '3 day' + interval '8 hour',
     CURRENT_TIMESTAMP + interval '4 day' + interval '20 hour'),

    (1, 3, 3, 8, 'Склад №4, Новосибирск', 54.9415, 82.9034, 'Гипермаркет "ОмскМаркет", Омск',
     55.0012, 73.4521, 3, 2500.0, 22.0, 'Продукты питания: мясо и молочные изделия', 7, 'REQ004',
     CURRENT_TIMESTAMP + interval '1 day' + interval '15 hour',
     CURRENT_TIMESTAMP + interval '2 day' + interval '10 hour'),

    (1, 5, 10, 16, 'Склад №5, Ростов-на-Дону', 47.2411, 39.6238, 'Склад №2, Краснодар',
     45.0425, 39.0541, 4, 1800.0, 20.0, 'Мебель: стулья и столы', 6, 'REQ005',
     CURRENT_TIMESTAMP + interval '2 day' + interval '12 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '15 hour'),

    (1, 1, 1, 6, 'Склад "Техно", Москва', 55.6543, 37.6341, 'Логистический центр "КазаньТранс"',
     55.7321, 49.1611, 6, 950.0, 10.0, 'Медицинские аппараты и расходные материалы', 10, 'REQ006',
     CURRENT_TIMESTAMP + interval '4 day' + interval '9 hour',
     CURRENT_TIMESTAMP + interval '6 day' + interval '18 hour'),

    (1, 1, 9, 52, 'Склад №1, Самара', 53.2015, 50.2518, 'Аптека №45, Саратов',
     51.5612, 46.0134, 10, 600.0, 8.5, 'Лекарственные препараты, термочувствительный груз', 5, 'REQ007',
     CURRENT_TIMESTAMP + interval '2 day' + interval '11 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '9 hour'),

    (1, 1, 14, 53, 'Фабрика "ТекстильПром", Воронеж', 51.6325, 39.1248, 'Склад-магазин "БелТекстиль", Белгород',
     50.6018, 36.6412, 7, 2200.0, 18.3, 'Текстильная продукция: ткани и постельное бельё', 3, 'REQ008',
     CURRENT_TIMESTAMP + interval '5 day' + interval '10 hour',
     CURRENT_TIMESTAMP + interval '7 day' + interval '14 hour'),

    (1, 1, 1, 54, 'Склад №9, Москва', 55.4312, 37.5541, 'ТЦ "ТулаМебель", Тула',
     54.1923, 37.5618, 8, 3100.0, 26.5, 'Бытовая техника: холодильники, стиральные машины', 9, 'REQ009',
     CURRENT_TIMESTAMP + interval '2 day' + interval '14 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '18 hour'),

    (1, 1, 31, 55, 'Склад Южный, Сочи', 43.5981, 39.7512, 'Порт Владивосток',
     43.1354, 131.9421, 9, 7200.0, 58.0, 'Автозапчасти: двигатели, колёса и кузовные элементы', 8, 'REQ010',
     CURRENT_TIMESTAMP + interval '10 day' + interval '10 hour',
     CURRENT_TIMESTAMP + interval '20 day' + interval '18 hour'),

    (1, 1, 2, 7, 'Склад "Север", Санкт-Петербург', 59.9343, 30.3351, 'Склад "Волга", Волгоград',
     48.7080, 44.5133, 2, 4100.0, 36.0, 'Строительные смеси и сухие материалы', 4, 'REQ011',
     CURRENT_TIMESTAMP + interval '3 day' + interval '9 hour',
     CURRENT_TIMESTAMP + interval '5 day' + interval '16 hour'),

    (1, 1, 11, 12, 'Логистический центр "Урал", Челябинск', 55.1644, 61.4368, 'Склад "БашЛогистик", Уфа',
     54.7351, 55.9587, 6, 1800.0, 12.5, 'Медоборудование и расходники', 10, 'REQ012',
     CURRENT_TIMESTAMP + interval '2 day' + interval '8 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '15 hour'),

    (1, 1, 15, 17, 'Склад "ЛипецкПром"', 52.6031, 39.5708, 'Склад "ТамбовТорг"',
     52.7213, 41.4523, 7, 2600.0, 19.0, 'Текстиль и фурнитура', 3, 'REQ013',
     CURRENT_TIMESTAMP + interval '4 day' + interval '7 hour',
     CURRENT_TIMESTAMP + interval '5 day' + interval '18 hour'),

    (1, 1, 18, 19, 'Склад "ЯрославльСнаб"', 57.6261, 39.8845, 'Склад "КостромаМаркет"',
     57.7677, 40.9264, 1, 900.0, 8.0, 'Компьютерные комплектующие', 1, 'REQ014',
     CURRENT_TIMESTAMP + interval '1 day' + interval '13 hour',
     CURRENT_TIMESTAMP + interval '2 day' + interval '11 hour'),

    (1, 1, 20, 21, 'Склад "ПензаЛогистик"', 53.2007, 45.0046, 'Склад "СаранскТорг"',
     54.1809, 45.1863, 5, 3200.0, 24.0, 'Металлические листы и профили', 4, 'REQ015',
     CURRENT_TIMESTAMP + interval '6 day' + interval '9 hour',
     CURRENT_TIMESTAMP + interval '7 day' + interval '20 hour'),

    (1, 1, 22, 23, 'Склад "ТюменьТранс"', 57.1530, 65.5343, 'Склад "КурганСклад"',
     55.4443, 65.3162, 3, 2100.0, 18.0, 'Продукты питания (охлаждение)', 7, 'REQ016',
     CURRENT_TIMESTAMP + interval '2 day' + interval '6 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '12 hour'),

    (1, 1, 24, 25, 'Склад "БарнаулСнаб"', 53.3489, 83.7764, 'Склад "ТомскЛогистик"',
     56.4846, 84.9476, 9, 6400.0, 50.0, 'Автозапчасти и агрегаты', 8, 'REQ017',
     CURRENT_TIMESTAMP + interval '7 day' + interval '8 hour',
     CURRENT_TIMESTAMP + interval '9 day' + interval '18 hour'),

    (1, 1, 26, 27, 'Склад "КировОпт"', 58.6036, 49.6679, 'Склад "ИжевскЛайн"',
     56.8526, 53.2045, 8, 2800.0, 21.0, 'Бытовая техника', 9, 'REQ018',
     CURRENT_TIMESTAMP + interval '3 day' + interval '11 hour',
     CURRENT_TIMESTAMP + interval '4 day' + interval '17 hour'),

    (1, 1, 28, 29, 'Склад "РязаньПром"', 54.6292, 39.7364, 'Склад "КалугаЛогистик"',
     54.5138, 36.2612, 6, 1400.0, 11.0, 'Медицинские расходники', 10, 'REQ019',
     CURRENT_TIMESTAMP + interval '2 day' + interval '10 hour',
     CURRENT_TIMESTAMP + interval '3 day' + interval '16 hour'),

    (1, 1, 30, 32, 'Склад "АрхангельскСнаб"', 64.5393, 40.5150, 'Склад "МурманскПорт"',
     68.9585, 33.0827, 4, 1900.0, 16.0, 'Мебель и комплектующие', 6, 'REQ020',
     CURRENT_TIMESTAMP + interval '8 day' + interval '9 hour',
     CURRENT_TIMESTAMP + interval '10 day' + interval '20 hour');
