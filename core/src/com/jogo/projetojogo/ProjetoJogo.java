package com.jogo.projetojogo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;


public class ProjetoJogo extends ApplicationAdapter {
	//criar animações
	private SpriteBatch batch;
	private Texture[] bonecos;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont menssagem;
	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	//private ShapeRenderer shape;

	//Atributos de configuração
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo = 0;
	private int pontuacao = 0;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto;

	//resolucao em diversas telas
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 1080;
	private final float VIRTUAL_HEIGHT = 1920;

	@Override
	public void create () {
        //inicializa o jogo
		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
//		retanguloCanoBaixo = new Rectangle();
//		retanguloCanoTopo = new Rectangle();
//		shape = new ShapeRenderer();
		//fontes que aparecem para a mensagem de game over e pontuaçao
		fonte = new BitmapFont();
		fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		fonte.getData().setScale(8);
		menssagem = new BitmapFont();
		menssagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		menssagem.getData().setScale(5);

		//arquivos necessarios para exibição
		bonecos = new Texture[3];
		bonecos[0] = new Texture("passaro1.png");
		bonecos[1] = new Texture("passaro2.png");
		bonecos[2] = new Texture("passaro2.png");
		fundo = new Texture("fundo.jpeg");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_Over.jpeg");

		//diversas resoluções
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		//define as variaveis do tamanho da tela para nao precisar chamar o metodo sempre
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturaDispositivo/2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;

	}

	@Override
	public void render () {
		camera.update();
		//limpar frames de execucoes anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		//Movimenta o boneco e suaviza o bater de asas
		variacao += deltaTime * 9;

		if (variacao > 2) {
			variacao = 0;
		}

		if (estadoJogo == 0 ){
			if(Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}
		else{
			//repetir a queda do passaro
			velocidadeQueda++;
			//quando clicar na tela, ele vai subir
			if (Gdx.input.justTouched()) {
				velocidadeQueda = -15;
			}

			if (estadoJogo == 1){
				//movimenta os canos
				if(pontuacao <= 10) {
					posicaoMovimentoCanoHorizontal -= deltaTime * 500;
				}else if (pontuacao >10 || pontuacao <=20){
					posicaoMovimentoCanoHorizontal -= deltaTime * 600;
				}else if (pontuacao >20 || pontuacao <=30){
					posicaoMovimentoCanoHorizontal -= deltaTime * 700;
				}else if (pontuacao >30 || pontuacao <=40){
					posicaoMovimentoCanoHorizontal -= deltaTime * 800;
				}else if (pontuacao >40 || pontuacao <=50){
					posicaoMovimentoCanoHorizontal -= deltaTime * 900;
				}
				//passaro vai caindo
				if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
					posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
				}
				//cano vai andando, ate chegar no final da tela e reseta
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					//quando sair da tela, reseta
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(1000) - 500;
					//define a marcacao de ponto como falso
					marcouPonto = false;
				}
				//verifica pontuação
				if(posicaoMovimentoCanoHorizontal < 200){
					//se ainda nao tiver marcado ponto, marcar
					if(!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
			}else {
				if(Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo/2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}
			}
		}
		//configura multipla resolucoes
		batch.setProjectionMatrix(camera.combined);
		//desenhar as imagens
		batch.begin();
		//deixa a imagem de fundo do tamanho do celular utilizado
		batch.draw(fundo, 0,0, larguraDispositivo, alturaDispositivo);
		//imagem do cano do topo da tela
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica );
		///imagem do cano na parte de baixo da tela
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica );

		//imagem alternando do passaro batendo asa
		batch.draw(bonecos[ (int)variacao], 200, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo-100);
		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			menssagem.draw(batch,"Toque para Reiniciar! ",larguraDispositivo/2 - 320, alturaDispositivo/2 - gameOver.getHeight()/2);
		}
		batch.end();


		passaroCirculo.set(200 + bonecos[0].getWidth()/2,
				posicaoInicialVertical + bonecos[0].getHeight()/2,
				bonecos[0].getWidth()/2);
		retanguloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		//desenhar as formas
//		shape.begin(ShapeRenderer.ShapeType.Filled);
//		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
//		shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//		shape.setColor(com.badlogic.gdx.graphics.Color.RED);
//		shape.end();

		//teste de colisão//se bateu o passaro no cano de baixo ou cano topo
        if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo) || posicaoInicialVertical <=0 || posicaoInicialVertical >= alturaDispositivo){
			estadoJogo = 2;
        }
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
