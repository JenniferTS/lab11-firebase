package com.gowayki.app.data

// Datos de RutaSeeder.php y ParaderoSeeder.php del prototipo web (PROTOTIPO-1-GOWAYKI)
object DatosIniciales {
    private fun p(nombre: String, lat: Double, lng: Double, orden: Int) = Paradero(nombre, lat, lng, orden)

    val rutas = listOf(
        Ruta("1", "Canarios S.A.", "Ruta que conecta el Mercado Avelino con TECSUP Arequipa, pasando por la Av. Salaverry.",
            "Mercado Avelino", "TECSUP Arequipa", 25, 1.00, "#3498DB", listOf(
                p("Mercado Avelino", -16.4250, -71.5300, 1), p("Av. Goyeneche", -16.4098, -71.5340, 2),
                p("Plaza de Armas", -16.3989, -71.5370, 3), p("Puente Grau", -16.3900, -71.5400, 4),
                p("Av. Ejército - San Lázaro", -16.3958, -71.5425, 5), p("Óvalo Lambramani", -16.4190, -71.5255, 6),
                p("Av. Dolores", -16.4235, -71.5175, 7), p("Hospital Honorio Delgado", -16.4140, -71.5220, 8),
                p("Av. Independencia - Cruce", -16.4060, -71.5270, 9), p("Real Plaza Cayma", -16.3928, -71.5488, 10),
                p("Av. Aviación", -16.3770, -71.5565, 11), p("Urb. La Estación", -16.4090, -71.5190, 12),
                p("TECSUP Arequipa", -16.4419, -71.5142, 13))),
        Ruta("2", "Cotum B", "Ruta desde la Plaza de Armas de Arequipa hasta el distrito de Cayma, pasando por el puente Grau.",
            "Plaza de Armas", "Cayma", 35, 1.00, "#27AE60", listOf(
                p("Plaza de Armas", -16.3989, -71.5370, 1), p("Puente Grau", -16.3900, -71.5400, 2), p("Cayma", -16.3647, -71.5575, 3))),
        Ruta("3", "El Conquistador", "Ruta del Terminal Terrestre hacia Yanahuara, ideal para turistas.",
            "Terminal Terrestre", "Yanahuara", 40, 1.20, "#F39C12", listOf(
                p("Terminal Terrestre", -16.4150, -71.5500, 1), p("Av. Ejército", -16.4050, -71.5450, 2), p("Yanahuara", -16.3892, -71.5508, 3))),
        Ruta("4", "San Martín", "Ruta expresa desde el Mercado San Camilo hasta el Mall Aventura.",
            "Mercado San Camilo", "Mall Aventura", 20, 1.00, "#8E44AD", listOf(
                p("Mercado San Camilo", -16.4027, -71.5337, 1), p("Av. Venezuela", -16.4100, -71.5250, 2), p("Mall Aventura", -16.4211, -71.5298, 3))),
        Ruta("5", "Arequipa - Sachaca", "Ruta desde la Plaza de Armas hasta el Mirador de Sachaca, con vista al valle.",
            "Plaza de Armas", "Mirador de Sachaca", 30, 1.00, "#E74C3C", listOf(
                p("Plaza de Armas", -16.3989, -71.5370, 1), p("Av. La Marina", -16.4100, -71.5450, 2), p("Mirador de Sachaca", -16.4267, -71.5591, 3)))
    )
}
