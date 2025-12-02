import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MascotaService } from '../services/mascota.service';
import { Mascota } from '../models/mascota.model';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private mascotaService = inject(MascotaService);

  public mascotas: Mascota[] = [];
  public isLoading = true;
  public error: string | null = null;

  // Datos estáticos de accesos destacados
  public accesoDestacado1 = {
    titulo: 'Ver mascotas en tu zona',
    descripcion: 'Encontrá mascotas perdidas cerca de tu ubicación.',
    imagen: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCpU5t3U4O7aon0QVl27VBFE0C6Nvni84n86ZUyNnTPz-SP87Rrful_gwaXd1xXIZU0hahGV_cv8pTpJFP-809qlWtIINQPDFTE0z9nseJgLEBNX3OPr002zQrTDD4EvQkWXTWtazwDYNzNHr_CN1PmmSmetBg71bPQ0c3Oft_wqhGEMsLlr5n89RJWiNwi9K7p2e2GxUiCom5kWwiA0trlWcMWAfIb7j7C9xjqKeKnGPhSgy-wCkFPKiz0Ums8Glyu48gHp6dG3ylW'
  };

  public accesoDestacado2 = {
    titulo: 'Ranking de Buscadores',
    descripcion: 'Mirá el ranking de personas que más ayudan.',
    imagen: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBRy-avtoky6XhB8AwHG6IbwtFpt9rT7EuBx5dp7q0iLan7LmTuTHUH7xWzfNXDFLweknyJlC1wstmUb2Uzv1BmmiR4bTItChdlKf4ggneG0L_04J8PSejFeFL90KL68-a1ypogzMkPzeoHG3FClymLms-a73zYM-Ev_agFAtV5p5abO0ISsIU9OPZG5O4F2eXrOWcsOfw9iAKlW_Pcj6Ufp2OXZ2a4O0cbAu4usL78C7hxyf5UAkr74cP71uOx6HIVyBprk_Qbup7U'
  };

  ngOnInit(): void {
    this.cargarMascotasPerdidas();
  }

  cargarMascotasPerdidas(): void {
    this.isLoading = true;
    this.mascotaService.obtenerMascotasPerdidas().subscribe({
      next: (mascotas) => {
        this.mascotas = mascotas;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar mascotas perdidas:', err);
        this.error = 'No se pudieron cargar las mascotas perdidas';
        this.isLoading = false;
      }
    });
  }

  obtenerImagenMascota(mascota: Mascota): string {
    // Si la mascota tiene fotos en base64, usar la primera
    if (mascota.fotos && mascota.fotos.length > 0) {
      return `data:image/jpeg;base64,${mascota.fotos[0]}`;
    }
    // Imagen por defecto si no hay foto
    return 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400';
  }
}
