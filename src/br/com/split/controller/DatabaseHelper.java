package br.com.split.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import br.com.split.model.Evento;
import br.com.split.model.Pessoa;
import br.com.split.model.Produto;

/**
 * Classe singleton para salvar e recuperar objetos do banco de dados. Também contém os métodos CRUD
 * @author Daniel
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private String[] tables;
	private static Context localContext;
	private static DatabaseHelper dbInstance;
	private static final String TAG = "DatabaseHelper";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "splitDb";
	// tabelas
	private static final String TABLE_PEOPLE = "pessoas";
	private static final String TABLE_PRODUCTS = "produtos";
	private static final String TABLE_EVENTS = "eventos";
	private static final String TABLE_PEOPLE_EVENTS = "pessoas_eventos";
	private static final String TABLE_PRODUCT_EVENTS = "produtos_eventos";
	private static final String TABLE_COMSUMPTION = "consumo";
	// campos tabela people
	private static final String PEOPLE_CLN_ID = "id_pessoa";
	private static final String PEOPLE_CLN_NAME = "nome";
	// campos tabela products
	private static final String PRODUCT_CLN_ID = "id_produto";
	private static final String PRODUCT_CLN_NAME = "nome";
	private static final String PRODUCT_CLN_PRICE = "preco_sugerido";
	// campos tabela evento
	private static final String EVENT_CLN_ID = "id_evento";
	private static final String EVENT_CLN_NAME = "nome";
	private static final String EVENT_CLN_START_DATE = "data_inicio";
	private static final String EVENT_CLN_END_DATE = "data_fim";
	// campos tabela pessoa_evento
	private static final String PEOPLEVENT_CLN_ID = "id_pessoa_evento";

	// campos tabela produto_evento
	private static final String PRODUCT_EVENT_CNL_ID = "id_produto_evento";
	private static final String PRODUCT_EVENT_CNL_PRICE = "preco";

	// campos tabela consumo
	private static final String CONSUM_CLN_ID = "id_consumo";
	private static final String CONSUM_CLN_QUANTITY = "quantidade";

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String createTablePeople = "CREATE TABLE " + TABLE_PEOPLE + "("
				+ PEOPLE_CLN_ID + " INTEGER PRIMARY KEY, "
				+ PEOPLE_CLN_NAME + " TEXT)";
		String createTableProducts = "CREATE TABLE " + TABLE_PRODUCTS + "("
				+ PRODUCT_CLN_ID + " INTEGER PRIMARY KEY, "
				+ PRODUCT_CLN_NAME + " TEXT, "
				+ PRODUCT_CLN_PRICE + " TEXT)";
		String createTableEvents = "CREATE TABLE " + TABLE_EVENTS + "("
				+ EVENT_CLN_ID + " INTEGER PRIMARY KEY, "
				+ EVENT_CLN_NAME + " TEXT, "
				+ EVENT_CLN_START_DATE + " INTEGER, "
				+ EVENT_CLN_END_DATE + " INTEGER, "
				+ PEOPLE_CLN_ID + " INTEGER, "
				+ "FOREIGN KEY(" + PEOPLE_CLN_ID + ") REFERENCES "
				+ TABLE_PEOPLE + "(" + PEOPLE_CLN_ID + "))";
		String createTablePeopleEvents = "CREATE TABLE " + TABLE_PEOPLE_EVENTS + "("
				+ PEOPLEVENT_CLN_ID + " INTEGER PRIMARY KEY, "
				+ EVENT_CLN_ID + " INTEGER, "
				+ PEOPLE_CLN_ID + " INTEGER, "
				+ "FOREIGN KEY(" + EVENT_CLN_ID + ") REFERENCES "
				+ TABLE_EVENTS + "(" + EVENT_CLN_ID + "), "
				+ "FOREIGN KEY(" + PEOPLE_CLN_ID + ") REFERENCES "
				+ TABLE_PEOPLE + "(" + PEOPLE_CLN_ID + "))";
		String createTableProductEvent = "CREATE TABLE " + TABLE_PRODUCT_EVENTS + "("
				+ PRODUCT_EVENT_CNL_ID + " INTEGER PRIMARY KEY, "
				+ PRODUCT_EVENT_CNL_PRICE + " TEXT, "
				+ EVENT_CLN_ID + " INTEGER, "
				+ PEOPLE_CLN_ID + " INTEGER, "
				+ "FOREIGN KEY( " + EVENT_CLN_ID + " ) REFERENCES "
				+ TABLE_EVENTS + "(" + EVENT_CLN_ID + "), "
				+ "FOREIGN KEY(" + PEOPLE_CLN_ID + ") REFERENCES "
				+ TABLE_PEOPLE + "(" + PEOPLE_CLN_ID + "))";
		String createTableComsumption = "CREATE TABLE " + TABLE_COMSUMPTION + "("
				+ CONSUM_CLN_ID + " INTEGER PRIMARY KEY, "
				+ CONSUM_CLN_QUANTITY + " INTEGER, "
				+ PRODUCT_EVENT_CNL_ID + " INTEGER, "
				+ PEOPLEVENT_CLN_ID + " INTEGER, "
				+ "FOREIGN KEY( " + PRODUCT_EVENT_CNL_ID + " ) REFERENCES "
				+ TABLE_PRODUCT_EVENTS + "(" + PRODUCT_EVENT_CNL_ID + "),"
				+ "FOREIGN KEY(" + PEOPLEVENT_CLN_ID + ") REFERENCES "
				+ TABLE_PEOPLE_EVENTS + "(" + PEOPLEVENT_CLN_ID + "))";

		String[] tablesToBeCreated = { createTablePeople,
				createTableProducts,
				createTableEvents,
				createTablePeopleEvents,
				createTableProductEvent,
				createTableComsumption };
		tables = tablesToBeCreated;
		Log.e(TAG, "Antes loop");
		for (String table : tablesToBeCreated) {
			Log.e(TAG, table);
			SQLiteStatement sqlStatement = db.compileStatement(table);
			sqlStatement.execute();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//Colocar os métodos para upgrade da base. É necessário mudar a versao do banco.
		//Por enquanto limpamos tudo.
		db.close();
		localContext.deleteDatabase(DATABASE_NAME);
		onCreate(db);
	}

	//Métodos crud
	public long adicionarPessoa(Pessoa pessoa)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(PEOPLE_CLN_NAME, pessoa.getNome());
		return db.insert(TABLE_PEOPLE, null, contents);
	}

	public int modificarPessoa(Pessoa pessoa)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(PEOPLE_CLN_NAME, pessoa.getNome());
		return db.update(TABLE_PEOPLE, contents, PEOPLE_CLN_ID + " = ? ",
				new String[] { String.valueOf(pessoa.getId()) });

	}

	public Pessoa getSinglePessoa(long id)
	{
		Pessoa newPessoa = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(false, TABLE_PEOPLE, new String[] { PEOPLE_CLN_ID, PEOPLE_CLN_NAME },
				PEOPLE_CLN_ID + " = ? ",
				new String[] { String.valueOf(id) },
				null, null, null, null, null);
		if (cursor.moveToFirst()) {
			long contactId = cursor.getLong(cursor.getColumnIndex(PEOPLE_CLN_ID));
			String name = cursor.getString(cursor.getColumnIndex(PEOPLE_CLN_NAME));
			// TODO: Por padrao, e pessoa esta sendo criada como nao sendo proveniente do facebook. VERIFICAR!
			newPessoa = new Pessoa(name, false);
			newPessoa.setId(contactId);
		}
		return newPessoa;
	}

	public List<Pessoa> getAllPessoas()
	{
		List<Pessoa> allPessoas = new ArrayList<Pessoa>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(false, TABLE_PEOPLE, null, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				long id = cursor.getLong(cursor.getColumnIndex(PEOPLE_CLN_ID));
				Pessoa pessoa = getSinglePessoa(id);
				allPessoas.add(pessoa);
			} while (cursor.moveToNext());
		}
		return allPessoas;
	}

	public List<Pessoa> getPessoasEvent(Evento evento)
	{
		//TODO: Pegar as pessoas do evento especifico
		return null;
	}

	//	
	//	public List<Pessoa> getTodasPessoas(){
	//		List<Pessoa> listaPessoas = new ArrayList<Pessoa>();
	//		SQLiteOpenHelper db = this.getReadableDatabase();
	//		Cursor cursor 
	//	}

	public long adicionarProduto(Produto produto)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(PRODUCT_CLN_NAME, produto.getNome());
		// Comando abaixo: Verificar se o getPreco é o que deve ser usado!
		// TODO: Importante! Este preco do produto nao deve ser o preco sugerido.
		contents.put(PRODUCT_CLN_PRICE, String.valueOf(produto.getPrecoSugerido()));
		return db.insert(TABLE_PRODUCTS, null, contents);
	}

	public long adicionarEvento(Evento evento, Pessoa proprietario)
	{
		//TODO: Referenciar o proprietario na tabela criada.
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(EVENT_CLN_NAME, evento.getNome());
		contents.put(PEOPLE_CLN_ID, proprietario.getNome());
		contents.put(EVENT_CLN_START_DATE, persistDate(evento.getDataInicio()));
		return db.insert(TABLE_EVENTS, null, contents);
	}

	public int modificarEvento(Evento evento)
	{
		//TODO: Criar modificacao
		return 0;
	}

	public long adicionarPessoaEvento(Evento evento, Pessoa pessoa)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(EVENT_CLN_ID, String.valueOf(evento.getId()));
		contents.put(PEOPLE_CLN_ID, String.valueOf(pessoa.getId()));
		return db.insert(TABLE_PEOPLE_EVENTS, null, contents);
	}

	public long adicionarProdutoEvento(Evento evento, Produto produto, int preco)
	{
		//TODO: Remover int preco e puxar do produto
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contents = new ContentValues();
		contents.put(EVENT_CLN_ID, String.valueOf(evento.getId()));
		contents.put(PRODUCT_CLN_ID, String.valueOf(produto.getId()));
		contents.put(PRODUCT_EVENT_CNL_PRICE, String.valueOf(preco));
		return db.insert(TABLE_PRODUCT_EVENTS, null, contents);
	}

	public long adicionarConsumo()
	{
		//TODO: Criar classe para o consumo
		return (long) 123123;
	}

	private DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper getInstance(Context context)
	{
		if (dbInstance == null) {
			dbInstance = new DatabaseHelper(context.getApplicationContext());
			localContext = context;
		}
		return dbInstance;
	}

	public static Long persistDate(Date date)
	{
		if (date != null) {
			return date.getTime();
		}
		return null;
	}

	public static Date loadDate(Cursor cursor, int index)
	{
		if (cursor.isNull(index)) {
			return null;
		}
		return new Date(cursor.getLong(index));
	}
}
