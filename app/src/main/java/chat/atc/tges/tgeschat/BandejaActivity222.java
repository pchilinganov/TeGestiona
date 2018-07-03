package chat.atc.tges.tgeschat;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.varPublicas.GlobalFunctions;


import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

public class BandejaActivity222 extends BaseActivity implements fragmentBandejaTicket1.OnFragmentInteractionListener , Comunicador {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private VolleyRP volley;
    private RequestQueue mRequest;
    TabLayout tabLayout;
    public static FragmentManager fragmentManager;
    android.support.v4.app.FragmentManager supportFragmentManager =  getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
    Comunicador oComunicador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_two);
        getSupportActionBar().setTitle("MovistarTAyuda");

        fragmentManager=getFragmentManager();

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_bandeja, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //permite modificar el hint que el EditText muestra por defecto
        //searchView.setQueryHint(getText(R.string.search));
        //searchView.setOnQueryTextListener(this);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              @Override
                                              public boolean onQueryTextSubmit(String query) { //Busca cuando se da click en lupa
                                                  if (mViewPager.getCurrentItem() ==1) {  // obtiene el índice de tab seleccionado
                                                      fragmentBandejaTicket2.nroConsulta=query;
                                                      fragmentBandejaTicket2.btnCarga.performClick();
                                                      //oComunicador.enviarDatos(query);
                                                      /*mViewPager.setCurrentItem(1);
                                                      PlaceholderFragment.newInstance( 1);*/

                                                  }
                                                  return true;
                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) { //busca cuando se va escribiendo en el control

                                                  if (mViewPager.getCurrentItem() ==0) {  // obtiene el índice de tab seleccionado
                                                      if (fragmentBandejaTicket1.mAdapter != null)
                                                          fragmentBandejaTicket1.mAdapter.getFilter().filter(newText);
                                                  }
                                                  return true;
                                              }
                                          }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //Toast.makeText(getApplicationContext(), "Buscar...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_Notificaciones) {
                mostrarDialogoIncidenciaMasiva();
            return true;
        }

        if (id == R.id.action_CerrarSesion) {
            logout();
            Intent intent = new Intent(BandejaActivity222.this, LoginTelefonica.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(BandejaActivity222.this,"Evento onResume de Activity Bandeja" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(BandejaActivity222.this,"Evento onPause de Activity Bandeja" , Toast.LENGTH_SHORT).show();
    }

    //Muestra Dialogo de Búsqueda de Expediente Proactivo
    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                estado="OK";
                GlobalFunctions.cancelAllNotification(getApplicationContext());
                //Toast.makeText(BandejaActivity.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BandejaActivity222.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idusuario", usuario);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void enviarDatos(String text) {
        fragmentBandejaTicket2 fragment = new fragmentBandejaTicket2();
        fragment.recibir("hola");
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_bandeja_ticket_one_fragment, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment=null;

            switch (sectionNumber){
                case 1:fragment= new fragmentBandejaTicket1();
                    break;
                case 2:fragment= new fragmentBandejaTicket2();
                    break;
            }
            return fragment;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "BANDEJA";
                case 1:
                    return "CONSULTA";
            }
            return null;
        }
    }
}
