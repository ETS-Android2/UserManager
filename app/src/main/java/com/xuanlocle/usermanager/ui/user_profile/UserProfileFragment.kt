package com.xuanlocle.usermanager.ui.user_profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.xuanlocle.usermanager.R
import com.xuanlocle.usermanager.data.model.UserProfileModel
import com.xuanlocle.usermanager.ui.base.BaseFragmentMVVM
import com.xuanlocle.usermanager.util.Constants
import com.xuanlocle.usermanager.util.image.ImageHelper
import kotlinx.android.synthetic.main.user_profile_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.random.Random

class UserProfileFragment : BaseFragmentMVVM<UserProfileViewModel>(), KodeinAware {

    companion object {
        fun newInstance(userLoginId: String): UserProfileFragment {
            val args = Bundle().apply {
                putString(Constants.BundleKey.USER_ID, userLoginId)
            }

            val fragment = UserProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val kodein by closestKodein()
    private val factory: UserProfileViewModelFactory by instance()

    override fun getLayout(): Int = R.layout.user_profile_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            if (args.containsKey(Constants.BundleKey.USER_ID)) {
                val userLoginId = args.getString(Constants.BundleKey.USER_ID) ?: ""
                if (userLoginId == "")
                    return
                viewModel.getProfile(userLoginId)
            }
        }
    }


    override fun initObserve() {
        viewModel.profileLiveData.observe(viewLifecycleOwner, { profile ->
            showProfileInfo(profile)
        })
    }

    private fun getRandomBackground(): Int {
        return when (Random.nextInt(3)) {
            0 -> R.drawable.bg1
            1 -> R.drawable.bg2
            2 -> R.drawable.bg3
            else -> R.drawable.bg3
        }
    }

    private fun showProfileInfo(profile: UserProfileModel) {
        ImageHelper.loadImageDrawableForShape(getRandomBackground(), imvBackground)
        ImageHelper.loadImageShapeable(profile.avatarURL, imvAvatar)
        tvProfileName.text =
            if (profile.name?.isNotEmpty() == true) profile.name else "Undefined"

        tvProfileLocation.text =
            if (profile.location?.isNotEmpty() == true) profile.location else "Undefined"

        tvFollowerCount.text = profile.followers.toString()
        tvFollowingCount.text = profile.following.toString()
        tvReposCount.text = profile.repos.toString()

        tvBioContent.text =
            if (profile.bio?.isNotEmpty() == true) profile.bio else "Nothing"
    }

    override fun init() {
        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)
        viewModel.init()
    }
}