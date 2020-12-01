import java.util.*;

public class ParPontosMaisProximos {

	static double menorDistancia = Integer.MAX_VALUE;
	static Point p1 = null, p2 = null;
	
	static final int TOTALPONTOS = 50_000;
	static final int MINPONTOS = 2;

	static Random sorteio = new Random();

	public static void main(String[] args) {

		/*
		 * Point p11 = new Point(2, 3); 
		 * Point p22 = new Point(12, 30); 
		 * Point p33 = new Point(40, 50); 
		 * Point p44 = new Point(5, 1); 
		 * Point p55 = new Point(12, 10);
		 * Point p66 = new Point(3, 4); 
		 * List<Point> list = new ArrayList<>();
		 * list.add(p11); 
		 * list.add(p22); 
		 * list.add(p33); 
		 * list.add(p44); 
		 * list.add(p55);
		 * list.add(p66);
		 */

		List<Point> list = geraPontos(TOTALPONTOS);

		menorDistancia(list);
		System.out.println("O par de pontos mais próximo é: (" + p1.x + "," + p1.y + ") (" + p2.x + "," + p2.y
				+ ") e a distância entre eles é " + menorDistancia);
	}

	public static List<Point> geraPontos(int quantPontos) {
		List<Point> pontos = new ArrayList<Point>(quantPontos);
		for (int i = 0; i < quantPontos; i++) {
			pontos.add(new Point(sorteio.nextInt(TOTALPONTOS), sorteio.nextInt(TOTALPONTOS)));
		}

		Point x = new Point(1000, 1000);
		Point y = new Point(1001, 1001);
		pontos.set(TOTALPONTOS / 2, x);
		pontos.set(TOTALPONTOS / 2 + 1, y);

		return pontos;
	}

	// Classe Point
	public static class Point {
		private int x;
		private int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private static double getMin() {
		return menorDistancia;
	}
	// Calcula o par de pontos mais próximos na matriz de pontos especificada

	public static void menorDistancia(List<Point> Pontos) {

		int n = Pontos.size();

		Point[] pontosPorX = new Point[n];
		
		
		for (int i = 0; i < n; i++) {
			pontosPorX[i] = Pontos.get(i);
		}

		// Ordenando por coordenada X
		Arrays.sort(pontosPorX, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.x != o2.x)
					return o1.x - o2.x;
				else
					return o1.y - o2.y;
			}
		});

		// verificar se há coincidência entre os pontos
		for (int i = 0; i < n - 1; i++) {
			if (pontosPorX[i] == pontosPorX[i + 1]) {
				menorDistancia = 0;
				p1 = pontosPorX[i];
				p2 = pontosPorX[i + 1];
				break;
			}
		}

		// Ordenar por coordenada Y - mas ainda não ordenado
		Point[] pontosPorY = new Point[n];
		for (int i = 0; i < n; i++)
			pontosPorY[i] = pontosPorX[i];

		// array auxiliar
		Point[] aux = new Point[n];

		maisProximos(pontosPorX, pontosPorY, aux, 0, n - 1);
	}

	// Encontrar o par de pontos mais próximos em PontosPorX
	// Condição prévia: pontosPorX e pontosPorY serem a mesma sequência de pontos
	// Pré condição: pontosPorX ordenados pela coordenada X
	// Pós condição: pontosPorY ordenados pela coordenada Y
	private static double maisProximos(Point[] pontosPorX, Point[] pontosPorY, Point[] aux, int comeco, int fim) {

		if (fim <= comeco)
			return Double.POSITIVE_INFINITY;

		//encontrar posição do meio
		int meio = comeco + (fim - comeco) / 2;
		
		//pegar o ponto (valor) na posição do meio
		Point mediana = pontosPorX[meio];

		// Calcula o par de pontos mais próximos com ambos os pontos de extremidades no
		// subarray da esquerda ou ambos no sub array da direita.

		double delta1 = maisProximos(pontosPorX, pontosPorY, aux, comeco, meio);
		double delta2 = maisProximos(pontosPorX, pontosPorY, aux, meio + 1, fim);
		double delta = Math.min(delta1, delta2);

		// mesclar novamente para que pontosPorY sejam ordenados pela coordenada Y
		merge(pontosPorY, aux, comeco, meio, fim);

		// aux[0..m-1] = sequencia de pontos mais proximo que o delta, ordenados pela
		// coordenada Y.
		int m = 0;
		for (int i = comeco; i <= fim; i++) {
			// retorna um valor absoluto
			if (Math.abs(pontosPorY[i].x - mediana.x) < delta)
				aux[m++] = pontosPorY[i];
		}

		// compara cada ponto com seus vizinho fixados na coordenada Y mais prox que o
		// delta
		
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; (j < m) && (aux[j].y - aux[i].y < delta); j++) {
				double distance = getDistance(aux[i], aux[j]);
				if (distance < delta) {
					delta = distance;
					if (distance < menorDistancia) {
						menorDistancia = delta;
						p1 = aux[i];
						p2 = aux[j];
					}
				}
			}
		}
		return delta;
	}

	private static void merge(Point[] a, Point[] aux, int comeco, int meio, int fim) {
		
		// copiar para o AUX
		for (int i = comeco; i <= fim; i++) {
			aux[i] = a[i];
		}

		// mesclar de volta para Point[] a
		int i = comeco, j = meio + 1;
		for (int k = comeco; k <= fim; k++) {
			if (i > meio)
				a[k] = aux[j++];
			else if (j > fim)
				a[k] = aux[i++];
			else if (menor(aux[j], aux[i]))
				a[k] = aux[j++];
			else
				a[k] = aux[i++];
		}
	}

	private static boolean menor(Point v, Point w) {
		return v.x < w.x;
	}

	// Retorna a distância Euclidiana entre o par de pontos mais próximo.
	public static double getDistance(Point a, Point b) {
		int x = a.x - b.x;
		int y = a.y - b.y;
		return Math.sqrt(x * x + y * y);
	}

}
